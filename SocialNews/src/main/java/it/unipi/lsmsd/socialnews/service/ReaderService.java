package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

public interface ReaderService {
    /**
     * Register a new reader in the application, storing the information into database
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

}
