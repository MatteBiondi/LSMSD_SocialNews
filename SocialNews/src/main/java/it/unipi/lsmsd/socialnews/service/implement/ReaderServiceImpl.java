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
import it.unipi.lsmsd.socialnews.service.util.Util;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ReaderServiceImpl implements ReaderService {
    /**
     * Registers a new reader in the application, storing the information into database
     *
     * @param newReader reader DTO object containing information of the new reader
     * @return identifier assigned to the new reader
     * @throws SocialNewsServiceException in case of failure of the insert operation on database
     */
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

    /**
     * Authenticates a reader identified by email via secret password
     *
     * @param email    email of the reader
     * @param password cleartext secret password of the reader
     * @return if authentication succeed readerDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
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

    /**
     * Retrieves all the information about the user identified by the email passed as argument
     *
     * @param email email of the reader
     * @return readerDTO object containing all the information
     * @throws SocialNewsServiceException in case of failure of the operation or if the reader is not in the system
     */
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

    /**
     * Publish a report related to a selected post
     *
     * @param newReport DTO object containing the information of the new report to publish
     * @param reporterId id of the reporter, owner of the interested post
     * @return the identifier assigned to the new report
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public Long publishReport(ReportDTO newReport, String reporterId) throws SocialNewsServiceException{
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

    /**
     * A reader (identify by id) starts to follow a reporter (identify by id)
     *
     * @param readerId id of the reader that starts to follow the reporter
     * @param reporterId id of the reporter that is followed
     * @return number of following relationship added to the database
     * @throws SocialNewsServiceException in case of failure of the creation operation on database
     */
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

    /**
     * Retrieve a list of the followed reporters, ordered by id, of a specified reader, up to a configured number of
     * reporters
     *
     * @param readerId id of the reader for which retrieve the followed reporters
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReporterDTO> firstPageFollowing(String readerId) throws SocialNewsServiceException{
        return nextPageFollowing(readerId, 0);
    }

    /**
     * Retrieve a list of the followed reporters, ordered by id, of a specified reader, up to a configured number of
     * reporters and starting from a certain offset
     *
     * @param readerId id of the reader for which retrieve the followed reporters
     * @param followingOffset integer containing the number of the last reporter in the previous page with respect the total
     *                        number of results
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReporterDTO> nextPageFollowing(String readerId, Integer followingOffset) throws SocialNewsServiceException{
        try {
            followingOffset = followingOffset != null ? followingOffset : Integer.valueOf(0);
            List<ReporterDTO> firstPageReporterDTO = new ArrayList<>();
            DAOLocator.getReaderDAO()
                    .getFollowingByReaderId(readerId,
                            Util.getIntProperty("listFollowingPageSize",25),
                            followingOffset )
                    .forEach(reporter -> firstPageReporterDTO.add(Util.buildReporterDTO(reporter)));
            return firstPageReporterDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    /**
     * A reader (identify by id) ends to follow a reporter (identify by id)
     *
     * @param readerId id of the reader that ends to follow the reporter
     * @param reporterId id of the reporter that is followed
     * @return number of following relationship removed from the database
     * @throws SocialNewsServiceException in case of failure of the deletion operation on database
     */
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

    /**
     * Retrieves information about reporters matching full name pattern ordered by name, up to a configured number of
     * reporters
     *
     * @param fullNamePattern full name regex pattern, matches all full names that contains a prefix in any of its word
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReporterDTO> firstPageReportersByFullName(String fullNamePattern) throws SocialNewsServiceException {
        return nextPageReportersByFullName(fullNamePattern, null);
    }

    /**
     * Retrieves information about reporters matching full name pattern ordered by name starting from the offset
     * passed as argument, up to a configured number of reporters
     *
     * @param fullNamePattern full name regex pattern, matches all full names that contains a prefix in any of its word
     * @param reporterOffset  reporter DTO containing the reporterId of the last reporter in the previous page
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public List<ReporterDTO> nextPageReportersByFullName(String fullNamePattern, ReporterDTO reporterOffset) throws SocialNewsServiceException {
        try {
            Reporter offset = reporterOffset == null ? null:Util.buildReporter(reporterOffset);
            List<ReporterDTO> pageReporterDTO = new ArrayList<>();
            DAOLocator.getReporterDAO()
                    .reportersByFullName(fullNamePattern, offset, Util.getIntProperty("listSearchReportersPageSize",50))
                    .forEach(reporter -> pageReporterDTO.add(Util.buildReporterDTO(reporter)));
            return pageReporterDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    /**
     * Retrieves a list of the suggested reporters to the reader identified by id passed as parameter
     *
     * @param readerId id of the reader that want the reporters suggestion
     * @return list of reporterDTO objects containing basic information of the suggested reporters
     * @throws SocialNewsServiceException in case of failure of the operation
     */
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
