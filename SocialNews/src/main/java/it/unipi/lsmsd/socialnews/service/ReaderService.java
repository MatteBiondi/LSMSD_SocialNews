package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
import it.unipi.lsmsd.socialnews.dto.ReportDTO;
import it.unipi.lsmsd.socialnews.dto.ReporterDTO;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

import java.util.List;

public interface ReaderService {
    /**
     * Registers a new reader in the application, storing the information into database
     *
     * @param newReader reader DTO object containing information of the new reader
     * @return identifier assigned to the new reader
     * @throws SocialNewsServiceException in case of failure of the insert operation on database
     */
    String register(ReaderDTO newReader) throws SocialNewsServiceException;

    /**
     * Authenticates a reader identified by email via secret password
     * @param email email of the reader
     * @param password cleartext secret password of the reader
     * @return if authentication succeed readerDTO object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    ReaderDTO authenticate(String email, String password) throws SocialNewsServiceException;

    /**
     * Retrieves all the information about the user identified by the email passed as argument
     *
     * @param email email of the reader
     * @return readerDTO object containing all the information
     * @throws SocialNewsServiceException in case of failure of the operation or if the reader is not in the system
     */
    ReaderDTO readerInfo(String email) throws SocialNewsServiceException;

    /**
     * Publish a report related to a selected post
     *
     * @param newReport DTO object containing the information of the new report to publish
     * @return the identifier assigned to the new report
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    Long publishReport(ReportDTO newReport) throws SocialNewsServiceException;

    /**
     * A reader (identify by id) starts to follow a reporter (identify by id)
     *
     * @param readerId id of the reader that starts to follow the reporter
     * @param reporterId id of the reporter that is followed
     * @return number of following relationship added to the database
     * @throws SocialNewsServiceException in case of failure of the creation operation on database
     */
    Integer followReporter(String readerId, String reporterId) throws SocialNewsServiceException;

    /**
     * Retrieve a list of the followed reporters, ordered by id, of a specified reader, up to a configured number of
     * reporters
     *
     * @param readerId id of the reader for which retrieve the followed reporters
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReporterDTO> firstPageFollowing(String readerId) throws SocialNewsServiceException;

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
    List<ReporterDTO> nextPageFollowing(String readerId, Integer followingOffset) throws SocialNewsServiceException;

    /**
     * A reader (identify by id) ends to follow a reporter (identify by id)
     *
     * @param readerId id of the reader that ends to follow the reporter
     * @param reporterId id of the reporter that is followed
     * @return number of following relationship removed from the database
     * @throws SocialNewsServiceException in case of failure of the deletion operation on database
     */
    Integer unfollowReporter(String readerId, String reporterId) throws SocialNewsServiceException;
    /**
     * Retrieves information about reporters matching full name pattern ordered by name, up to a configured number of
     * reporters
     *
     * @param fullNamePattern full name regex pattern, matches all full names that contains a prefix in any of its word
     * @return list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReporterDTO> firstPageReportersByFullName(String fullNamePattern) throws SocialNewsServiceException;

    /**
     * Retrieves information about reporters matching full name pattern ordered by name starting from the offset
     * passed as argument, up to a configured number of reporters
     *
     * @param fullNamePattern full name regex pattern, matches all full names that contains a prefix in any of its word
     * @param reporterOffset reporter DTO containing the reporterId of the last reporter in the previous page
     * @return  list of reporterDTO objects containing basic information
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReporterDTO> nextPageReportersByFullName(String fullNamePattern, ReporterDTO reporterOffset) throws SocialNewsServiceException;

    /**
     * Retrieves a list of the suggested reporters to the reader identified by id passed as parameter
     *
     * @param readerId id of the reader that want the reporters suggestion
     * @return list of reporterDTO objects containing basic information of the suggested reporters
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    List<ReporterDTO> readSuggestedReporters (String readerId) throws SocialNewsServiceException;
}
