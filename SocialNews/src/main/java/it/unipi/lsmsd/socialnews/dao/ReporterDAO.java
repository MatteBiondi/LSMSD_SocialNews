package it.unipi.lsmsd.socialnews.dao;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import java.util.List;

public interface ReporterDAO {
    /**
     * Insert new reporter into the database
     *
     * @param newReporter reporter object containing information of the new reporter
     * @return identifier assigned to the new reporter
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
    String register(Reporter newReporter) throws SocialNewsDataAccessException;

    /**
     * Authenticates the reporter identified by email via secret password
     *
     * @param email email of the reporter
     * @param password password to compare with the one saved on database
     * @return if authentication succeed reporter object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    Reporter authenticate(String email, String password) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about the reporter identified by email field
     *
     * @param email email of the reporter
     * @return reporter object containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    Reporter reporterByEmail(String email) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about a reporter, including all his\her posts, limiting the post list size to the
     * dimension specified
     *
     * @param reporterId reporter identifier
     * @param pageSize number of posts to retrieve
     * @return reporter objects containing all the information, including posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    Reporter reporterByReporterId(String reporterId, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the reporter saved on database that matches the fullName specified, excluding
     * posts, limiting the list size to the dimension specified, starting from the reporter specified as argument in
     * reverse order.
     * It allows the implementation of pagination of the reporters
     *
     * @param fullNamePattern full name regex pattern, matches all full names that contains a prefix in any of its word
     * @param offset reporter from which the query starts to retrieve information
     * @param pageSize number of reporters to retrieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> reportersByFullNamePrev(String fullNamePattern, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the reporter saved on database that matches the fullName specified, excluding
     * posts, limiting the list size to the dimension specified, starting from the reporter specified as argument.
     * It allows the implementation of pagination of the reporters
     *
     * @param fullNamePattern full name regex pattern, matches all full names that contains a prefix in any of its word
     * @param offset reporter from which the query starts to retrieve information
     * @param pageSize number of reporters to retrieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> reportersByFullNameNext(String fullNamePattern, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the reporter saved on database, excluding posts, limiting the list size to the
     * dimension specified, starting from the reporter specified as argument in reverse order. It allows the
     * implementation of
     * pagination of the reporters
     *
     * @param filter reporter object containing email pattern used to filter readers
     * @param offset reporter from which the query starts to retrieve information
     * @param pageSize ieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> allReportersPrev(Reporter filter, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the reporter saved on database, excluding posts, limiting the list size to the
     * dimension specified, starting from the reporter specified as argument. It allows the implementation of
     * pagination of the reporters
     *
     * @param filter reporter object containing email pattern used to filter readers
     * @param offset reporter from which the query starts to retrieve information
     * @param pageSize ieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> allReportersNext(Reporter filter, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Remove a reporter, all posts and associated comments from the database
     *
     * @param reporterId reporter identifier
     * @return number of reporter removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    Long removeReporter(String reporterId) throws SocialNewsDataAccessException;

    /**
     * Retrieve the number of followers for a given reporter
     *
     * @param reporterId id of the reporter
     * @return number of follower for the reporter identified by 'reporterId'
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    Integer getNumOfFollowers(String reporterId) throws SocialNewsDataAccessException;

    /**
     * Retrieve the number of followers for a given reporter and if the reader is one of the follower
     *
     * @param reporterId id of the reporter
     * @param readerId id of the reader
     * @return ObjectNode containing:
     * 1) The number of follower for the reporter identified by 'reporterId'
     * 2) A field 'follower' containing '1' if the reader is one of the followers, '0' otherwise
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    ObjectNode getNumOfFollowers(String reporterId, String readerId) throws SocialNewsDataAccessException;

    /**
     * Retrieve the most popular reporters. Popularity is given by the number of followers
     *
     * @param limitTopRanking number of retrieved reporters
     * @return ArrayNode of reporter with basic information (id, name and picture) and number of followers
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    ArrayNode getMostPopularReporters(int limitTopRanking) throws SocialNewsDataAccessException;

    /**
     * Checks the current size of the reporter into the database and performs some maintenance operations if needed
     *
     * @param email of the reporter
     * @return true if documents have been changed, false otherwise
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    Boolean checkAndSwap(String email) throws SocialNewsDataAccessException;
}
