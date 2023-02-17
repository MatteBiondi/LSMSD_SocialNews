package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Admin;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import it.unipi.lsmsd.socialnews.dto.*;
import it.unipi.lsmsd.socialnews.service.AdminService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Page;
import it.unipi.lsmsd.socialnews.service.util.ServiceWorkerPool;
import it.unipi.lsmsd.socialnews.service.util.Statistic;
import it.unipi.lsmsd.socialnews.service.util.Util;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AdminServiceImpl implements AdminService {

    private List<ReaderDTO> pageReaders(ReaderDTO readerFilter, ReaderDTO readerOffset, Page page) throws SocialNewsServiceException{
        try {
            Reader offset = (readerOffset == null) ? null:Util.buildReader(readerOffset);
            Reader filter = (readerFilter == null) ? null:Util.buildReader(readerFilter);
            List<ReaderDTO> pageReaderDTO = new ArrayList<>();

            switch (page){
                case FIRST, NEXT -> DAOLocator.getReaderDAO()
                        .allReadersNext(filter, offset, Util.getIntProperty("listUserPageSize",25))
                        .forEach(reader -> pageReaderDTO.add(Util.buildReaderDTO(reader)));
                case PREV -> DAOLocator.getReaderDAO()
                        .allReadersPrev(filter, offset, Util.getIntProperty("listUserPageSize",25))
                        .forEach(reader -> pageReaderDTO.add(Util.buildReaderDTO(reader)));
           }
            return pageReaderDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error: " + ex.getMessage());
        }
    }

    private List<ReporterDTO> pageReporters(ReporterDTO reporterFilter, ReporterDTO reporterOffset, Page page) throws SocialNewsServiceException{
        try {
            Reporter offset = (reporterOffset == null) ? null:Util.buildReporter(reporterOffset);
            Reporter filter = (reporterFilter == null) ? null:Util.buildReporter(reporterFilter);
            List<ReporterDTO> pageReporterDTO = new ArrayList<>();

            switch (page){
                case FIRST, NEXT -> DAOLocator.getReporterDAO()
                        .allReportersNext(filter, offset, Util.getIntProperty("listUserPageSize",25))
                        .forEach(reporter -> pageReporterDTO.add(Util.buildReporterDTO(reporter)));
                case PREV ->
                        DAOLocator.getReporterDAO()
                                .allReportersPrev(filter, offset, Util.getIntProperty("listUserPageSize",25))
                                .forEach(reporter -> pageReporterDTO.add(Util.buildReporterDTO(reporter)));
            }
            return pageReporterDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error: " + ex.getMessage());
        }
    }

    @Override
    public String registerReporter(ReporterDTO newReporter) throws SocialNewsServiceException {
        try {
            newReporter.setPassword(Util.hashPassword(newReporter.getPassword()));
            return DAOLocator.getReporterDAO().register(Util.buildReporter(newReporter));
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Configuration error: hash algorithm");
        }
    }

    @Override
    public AdminDTO authenticate(String email, String password) throws SocialNewsServiceException {
        try {
            Admin admin = DAOLocator.getAdminDAO().authenticate(email, Util.hashPassword(password));
            System.out.println(admin);
            return Util.buildAdminDTO(admin);
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
        catch (NoSuchAlgorithmException ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Configuration error: hash algorithm");
        }
    }

    @Override
    public List<ReaderDTO> firstPageReaders(ReaderDTO readerFilter) throws SocialNewsServiceException {
        return pageReaders( readerFilter, null, Page.FIRST);
    }

    @Override
    public List<ReaderDTO> prevPageReaders(ReaderDTO readerFilter, ReaderDTO readerOffset) throws SocialNewsServiceException {
        return pageReaders(readerFilter, readerOffset, Page.PREV);
    }

    @Override
    public List<ReaderDTO> nextPageReaders(ReaderDTO readerFilter, ReaderDTO readerOffset) throws SocialNewsServiceException {
        return pageReaders( readerFilter, readerOffset, Page.NEXT);
    }

    @Override
    public List<ReporterDTO> firstPageReporters(ReporterDTO reporterFilter) throws SocialNewsServiceException {
        return pageReporters(reporterFilter, null, Page.FIRST);
    }

    @Override
    public List<ReporterDTO> prevPageReporters(ReporterDTO reporterFilter, ReporterDTO reporterOffset) throws SocialNewsServiceException {
        return pageReporters(reporterFilter, reporterOffset, Page.PREV);
    }

    @Override
    public List<ReporterDTO> nextPageReporters(ReporterDTO reporterFilter,ReporterDTO reporterOffset) throws SocialNewsServiceException {
        return pageReporters(reporterFilter, reporterOffset, Page.NEXT);
    }

    @Override
    public List<ReportDTO> firstPageReports(String reporterId) throws SocialNewsServiceException{
        return nextPageReports(reporterId, 0);
    }

    @Override
    public List<ReportDTO> nextPageReports(String reporterId, Integer reportOffset) throws SocialNewsServiceException{
        try {
            reportOffset = reportOffset != null ? reportOffset : Integer.valueOf(0);
            List<ReportDTO> firstPageReportDTO = new ArrayList<>();
            DAOLocator.getReportDAO()
                    .getReportsByReporterId(reporterId,
                            Util.getIntProperty("listReportPageSize",25),
                            reportOffset )
                    .forEach(report -> firstPageReportDTO.add(Util.buildReportDTO(report)));
            return firstPageReportDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    @Override
    public void removeReader(String toRemoveReaderId) throws SocialNewsServiceException {
        try {
            Long removedCounter = DAOLocator.getReaderDAO().removeReader(toRemoveReaderId);
            if (removedCounter == 0){
                throw new SocialNewsServiceException("Reader not in the system");
            }
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    @Override
    public void removeReporter(String toRemoveReporterId) throws SocialNewsServiceException {
        try {
            Long removedCounter = DAOLocator.getReporterDAO().removeReporter(toRemoveReporterId);
            if (removedCounter == 0){
                throw new SocialNewsServiceException("Reporter not in the system");
            }
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    @Override
    public void removeReport(String reportId) throws SocialNewsServiceException{
        try{
            int removedCounter = DAOLocator.getReportDAO().deleteReport(reportId);
            if (removedCounter == 0){
                throw new SocialNewsServiceException("Report not in the system");
            }
        }catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    @Override
    public StatisticPageDTO computeStatistics(Statistic... statistics) throws SocialNewsServiceException {
        try {
            HashMap<String, Object> computedStatistics = new HashMap<>();
            HashMap<String, Future<?>> futures = new HashMap<>();

            for(Statistic statistic: statistics){
                futures.put(
                        statistic.toString(),
                        switch (statistic){
                            case MOST_ACTIVE_READERS -> ServiceWorkerPool.getPool().submitTask(
                                    () -> DAOLocator.getCommentDAO()
                                            .latestMostActiveReaders(
                                                    Util.getIntProperty("topNReaders",10),
                                                    Date.from(LocalDateTime.now().minus(statistic.getLastN(),
                                                                    statistic.getUnitOfTime())
                                                            .atZone(ZoneOffset.systemDefault()).toInstant()
                                                    )));
                            case GENDER_STATISTIC-> ServiceWorkerPool.getPool().submitTask(
                                    () -> DAOLocator.getReaderDAO().genderStatistic());
                            case NATIONALITY_STATISTIC-> ServiceWorkerPool.getPool().submitTask(
                                    () -> DAOLocator.getReaderDAO().nationalityStatistic());
                            case MOST_POPULAR_REPORTERS-> ServiceWorkerPool.getPool().submitTask(
                                    () -> DAOLocator.getReporterDAO().getMostPopularReporters(
                                            Util.getIntProperty("topNReporters", 10))
                            );
                        }
                );
            }
            for (Statistic statistic: statistics){
                computedStatistics.put(statistic.toString(), futures.get(statistic.toString()).get());
            }
            return new StatisticPageDTO(computedStatistics);
        }
        catch (NullPointerException ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Missing some args");
        }
        catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error: " + ex.getMessage());
        }
    }

    @Override
    public List<ReporterDTO> rankReportersByPopularity() throws SocialNewsServiceException{
        try {
            List<ReporterDTO> listReporterDTO = new ArrayList<>();
            DAOLocator.getReporterDAO()
                    .getMostPopularReporters(Util.getIntProperty("listReportersPopularityRank",5))
                    .forEach(reporter -> listReporterDTO.add(Util.buildReporterDTO(reporter)));
            return listReporterDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

}
