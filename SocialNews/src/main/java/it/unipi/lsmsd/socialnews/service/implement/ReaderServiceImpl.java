package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import it.unipi.lsmsd.socialnews.dto.ReaderDTO;
import it.unipi.lsmsd.socialnews.service.ReaderService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Util;

import java.security.NoSuchAlgorithmException;

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
}
