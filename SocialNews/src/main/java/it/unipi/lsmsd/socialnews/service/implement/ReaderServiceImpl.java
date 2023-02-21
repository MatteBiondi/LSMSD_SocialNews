package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
import it.unipi.lsmsd.socialnews.dto.ReportDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.service.ReaderService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Page;
import it.unipi.lsmsd.socialnews.service.util.Util;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ReaderServiceImpl implements ReaderService {

    private List<ReporterDTO> pageReportersByFullName(String fullNamePattern, ReporterDTO reporterOffset, Page page) throws SocialNewsServiceException{
        try {
            Reporter offset = reporterOffset == null ? null:Util.buildReporter(reporterOffset);
            List<ReporterDTO> pageReporterDTO = new ArrayList<>();
            switch (page){
                case FIRST, NEXT -> DAOLocator.getReporterDAO()
                        .reportersByFullNameNext(fullNamePattern, offset, Util.getIntProperty(
                                "listSearchReportersPageSize",50))
                        .forEach(reporter -> pageReporterDTO.add(Util.buildReporterDTO(reporter)));
                case PREV -> DAOLocator.getReporterDAO()
                        .reportersByFullNamePrev(fullNamePattern, offset, Util.getIntProperty(
                                "listSearchReportersPageSize",50))
                        .forEach(reporter -> pageReporterDTO.add(Util.buildReporterDTO(reporter)));
            }
            return pageReporterDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error: " + ex.getMessage());
        }
    }

    @Override
    public String register(ReaderDTO newReader) throws SocialNewsServiceException {
        try {
            newReader.setPassword(Util.hashPassword(newReader.getPassword()));
            return DAOLocator.getReaderDAO().register(Util.buildReader(newReader));
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Configuration error: hash algorithm");
        }
    }

    @Override
    public ReaderDTO authenticate(String email, String password) throws SocialNewsServiceException {
        try {
            Reader reader = DAOLocator.getReaderDAO().authenticate(email, Util.hashPassword(password));
            return Util.buildReaderDTO(reader);
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
    public ReaderDTO readerInfo(String email) throws SocialNewsServiceException {
        try {
            Reader reader = DAOLocator.getReaderDAO().readerByEmail(email);
            return Util.buildReaderDTO(reader);
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
        catch (IllegalArgumentException ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("User not in the system, check the email field");
        }
    }

    @Override
    public String publishReport(ReportDTO newReport, String reporterId) throws SocialNewsServiceException{
        try {
            return DAOLocator.getReportDAO().addReport(Util.buildReport(newReport), reporterId);
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Error in report publication. Retry again.");
        }
    }

    @Override
    public Integer followReporter(String readerId, String reporterId) throws SocialNewsServiceException{
        try {
            return DAOLocator.getReaderDAO().followReporter(readerId, reporterId);
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Error in follow operation. Retry again.");
        }
    }

    @Override
    public List<ReporterDTO> firstPageFollowing(String readerId) throws SocialNewsServiceException{
        return nextPageFollowing(readerId, 0);
    }

    @Override
    public List<ReporterDTO> nextPageFollowing(String readerId, Integer followingOffset) throws SocialNewsServiceException{
        try {
            followingOffset = followingOffset != null ? followingOffset : Integer.valueOf(0);
            List<ReporterDTO> firstPageReporterDTO = new ArrayList<>();
            DAOLocator.getReaderDAO()
                    .getFollowingByReaderId(readerId,
                            Util.getIntProperty("listFollowingPageSize",10),
                            followingOffset )
                    .forEach(reporter -> firstPageReporterDTO.add(Util.buildReporterDTO(reporter)));
            return firstPageReporterDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    @Override
    public Integer unfollowReporter(String readerId, String reporterId) throws SocialNewsServiceException{
        try {
            return DAOLocator.getReaderDAO().unfollowReporter(readerId, reporterId);
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw new SocialNewsServiceException("Error in unfollow operation. Retry again.");
        }
    }

    @Override
    public List<ReporterDTO> firstPageReportersByFullName(String fullNamePattern) throws SocialNewsServiceException {
        return pageReportersByFullName(fullNamePattern, null, Page.FIRST);
    }

    @Override
    public List<ReporterDTO> prevPageReportersByFullName(String fullNamePattern, ReporterDTO reporterOffset) throws SocialNewsServiceException {
        return pageReportersByFullName(fullNamePattern, reporterOffset, Page.PREV);
    }

    @Override
    public List<ReporterDTO> nextPageReportersByFullName(String fullNamePattern, ReporterDTO reporterOffset) throws SocialNewsServiceException {
        return pageReportersByFullName(fullNamePattern, reporterOffset, Page.NEXT);
    }

    @Override
    public List<ReporterDTO> readSuggestedReporters (String readerId) throws SocialNewsServiceException{
        try {
            List<ReporterDTO> listReporterDTO = new ArrayList<>();
            DAOLocator.getReaderDAO()
                    .suggestReporters(readerId, Util.getIntProperty("listSuggestedReportersSize",10))
                    .forEach(reporter -> listReporterDTO.add(Util.buildReporterDTO(reporter)));
            return listReporterDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }
}
