package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Admin;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import it.unipi.lsmsd.socialnews.dto.*;
import it.unipi.lsmsd.socialnews.service.AdminService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
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

    /**
     * Registers a new reporter in the application, storing the information on database
     *
     * @param newReporter reporter DTO object containing information of the new reporter
     * @return identifier assigned to the new reporter
     * @throws SocialNewsServiceException in case of failure of the operation
     */
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

    /**
     * Authenticates an admin identified by email via secret password
     *
     * @param email    email of the admin
     * @param password cleartext secret password of the admin
     * @return if authentication succeed adminDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
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

    /**
     * Retrieves information about readers ordered by name, up to a configured number of readers
     *
     * @return list of readerDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReaderDTO> firstPageReaders() throws SocialNewsServiceException {
        return nextPageReaders(null);
    }

    /**
     * Retrieves information about readers ordered by name starting from the offset passed as argument, up to a
     * configured number of readers
     *
     * @param readerOffset reader DTO containing id and fullName of the last reader in the previous page
     * @return list of readerDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReaderDTO> nextPageReaders(ReaderDTO readerOffset) throws SocialNewsServiceException {
        try {
            Reader offset = readerOffset == null ? null:Util.buildReader(readerOffset);
            List<ReaderDTO> firstPageReaderDTO = new ArrayList<>();
            DAOLocator.getReaderDAO()
                    .allReaders(offset, Util.getIntProperty("listUserPageSize",50))
                    .forEach(reader -> firstPageReaderDTO.add(Util.buildReaderDTO(reader)));
            return firstPageReaderDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    /**
     * Retrieves information about reporters ordered by name, up to a configured number of reporters
     *
     * @return list of reportersDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReporterDTO> firstPageReporters() throws SocialNewsServiceException {
        return nextPageReporters(null);
    }

    /**
     * Retrieves information about reporters ordered by name starting from the offset passed as argument, up to a
     * configured number of reporters
     *
     * @param reporterOffset reporter DTO containing reporterId and fullName of the last reporter in the previous page
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReporterDTO> nextPageReporters(ReporterDTO reporterOffset) throws SocialNewsServiceException {
        try {
            Reporter offset = reporterOffset == null ? null:Util.buildReporter(reporterOffset);
            List<ReporterDTO> firstPageReporterDTO = new ArrayList<>();
            DAOLocator.getReporterDAO()
                    .allReporters(offset, Util.getIntProperty("listUserPageSize",50))
                    .forEach(reporter -> firstPageReporterDTO.add(Util.buildReporterDTO(reporter)));
            return firstPageReporterDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }


    /**
     * Retrieves information about report, ordered by id, associated to a reporter, up to a configured number of report
     *
     * @param reporterId id of the reporter for which retrieve associated reports
     * @return list of reportDTO objects containing all the information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReportDTO> firstPageReports(String reporterId) throws SocialNewsServiceException{
        return nextPageReports(reporterId, 0);
    }


    /**
     * Retrieves information about reports (ordered by id starting), from the offset passed as argument, of a reporter,
     * up to a configured number of reports
     *
     * @param reporterId id of the reporter for which retrieve associated reports
     * @param reportOffset integer containing the number of the last report in the previous page with respect the total
     *                     number of results
     * @return list of reportDTO objects containing all the information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
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

    /**
     * Remove a reader from the databases
     *
     * @param toRemoveReaderId id associated to the reader to remove
     * @throws SocialNewsServiceException in case of failure of the remove operation
     */
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

    /**
     * Remove a reporter from the databases
     *
     * @param toRemoveReporterId id associated to the reporter to remove
     * @throws SocialNewsServiceException in case of failure of the remove operation
     */
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

    /**
     * Remove a report from the database
     *
     * @param reportId id associated to the report to remove
     * @throws SocialNewsServiceException in case of failure of the remove operation
     */
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

    /**
     * Computes the statistics specified by arguments and pack them into a DTO containing the results
     *
     * @param statistics series of statistics that must be computed
     * @return statistic results grouped into a DTO objects
     * @throws SocialNewsServiceException in case of failure of the operation
     */
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
                            case HOTTEST_MOMENTS_OF_DAY-> ServiceWorkerPool.getPool().submitTask(
                                    () -> DAOLocator.getCommentDAO().latestHottestMomentsOfDay(
                                    statistic.getWindowSize(),
                                    Date.from(LocalDateTime.now().minus(statistic.getLastN(), statistic.getUnitOfTime())
                                            .atZone(ZoneOffset.systemDefault()).toInstant()
                                    )));
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
            throw new SocialNewsServiceException("Database error");
        }
    }

    /**
     * Retrieve the top 5 most popular reporters of the system
     *
     * @return list of ReporterDTO objects containing basic information of the most popular reporters
     * @throws SocialNewsServiceException in case of failure of the operation
     */
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
