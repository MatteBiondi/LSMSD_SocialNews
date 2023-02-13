package it.unipi.lsmsd.socialnews.dao;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;

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
     * @param filter reader object containing email pattern used to filter readers
     * @param offset reader from which the query starts to retrieve information in revers order
     * @param pageSize number of readers to retrieve
     * @return list of reader objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reader> allReadersPrev(Reader filter, Reader offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the readers saved on database, limiting the list size to the dimension specified,
     * starting from the reader specified as argument. It allows the implementation of pagination of the readers
     *
     * @param filter reader object containing email pattern used to filter readers
     * @param offset reader from which the query starts to retrieve information
     * @param pageSize number of readers to retrieve
     * @return list of reader objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reader> allReadersNext(Reader filter, Reader offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     *Remove a reader from the database and the associated comments
     *
     * @param readerId email of the reader to remove
     * @return number of reader removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    Long removeReader(String readerId) throws SocialNewsDataAccessException;

    /**
     * Add a following relationship between a reader and a reporter
     *
     * @param readerId id of the reader
     * @param reporterId id of the reporter
     * @return number of following relationship created
     * @throws SocialNewsDataAccessException in case of failure of the creation operation of following relation
     */
    Integer followReporter(String readerId, String reporterId) throws SocialNewsDataAccessException;

    /**
     * Retrieve the followed reporters of a certain reader
     *
     * @param readerId id of the reader
     * @param limit maximum number of reporters to retrieve per request
     * @param offset reporter from which the query starts to retrieve information
     * @return list of reporter objects containing basic information (id, name and picture)
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> getFollowingByReaderId(String readerId, int limit, int offset) throws SocialNewsDataAccessException;

    /**
     * Remove a following relationship between a reader and a reporter
     *
     * @param readerId id of the reader
     * @param reporterId id of the reporter
     * @return number of following relationship removed from the database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    Integer unfollowReporter(String readerId, String reporterId) throws SocialNewsDataAccessException;

    /**
     * Given a reader, suggest most popular reporters that are not in the reader's following
     *
     * @param readerId id of the reader
     * @param limitListLen number of suggested reporters
     * @return list of reporter objects containing basic information (id, name and picture)
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> suggestReporters(String readerId, int limitListLen) throws SocialNewsDataAccessException;

    /**
     * Compute the number of registered readers grouped by male\female\others
     *
     * @return JSON object containing the information computed by aggregation pipeline
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    ObjectNode genderStatistic() throws SocialNewsDataAccessException;

    /**
     * Compute the number of registered readers grouped by his/her nationality
     *
     * @return JSON object containing the information computed by aggregation pipeline
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    ArrayNode nationalityStatistic() throws SocialNewsDataAccessException;
}
