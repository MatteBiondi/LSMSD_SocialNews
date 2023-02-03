package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import java.util.List;

public interface ReaderDAO {
    /**
     * Insert new reader into the database
     *
     * @param newReader reader object containing information of the new reader
     * @return identifier assigned to the new reader
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
    String register(Reader newReader) throws SocialNewsDataAccessException;


    /**
     * Authenticates the reader identified by email via secret password
     *
     * @param email email of the reader
     * @param password password to compare with the one saved on database
     * @return if authentication succeed reader object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    Reader authenticate(String email, String password) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about the reader identified by email field
     *
     * @param email email of the reader
     * @return reader object containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    Reader readerByEmail(String email) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the readers saved on database, limiting the list size to the dimension specified
     *
     * @param pageSize number of readers to retrieve
     * @return list of reader objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reader> allReaders(Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the readers saved on database, limiting the list size to the dimension specified,
     * starting from the reader specified as argument. It allows the implementation of pagination of the readers
     *
     * @param offset reader from which the query starts to retrieve information
     * @param pageSize number of readers to retrieve
     * @return list of reader objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reader> allReaders(Reader offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Remove a reader from the database
     *
     * @param email email of the reader to remove
     * @return number of reader removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    Long removeReader(String email) throws SocialNewsDataAccessException;
}
