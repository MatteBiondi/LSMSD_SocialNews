package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
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
}
