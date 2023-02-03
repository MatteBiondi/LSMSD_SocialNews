package it.unipi.lsmsd.socialnews.dao;

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
    Reporter authenticate(String email, String password) throws SocialNewsDataAccessException;//TODO: include posts ?

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
     * Retrieves information about a reporter, including all his\her posts, limiting the post list size to the
     * dimension specified, starting to the posts specified as argument. It allows the implementation of pagination
     * of the posts
     *
     * @param reporterId reporter identifier
     * @param offset post from which the query starts to retrieve information
     * @param pageSize number of posts to retrieve
     * @return reporter objects containing all the information, including posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    Reporter reporterByReporterId(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the reporters saved on database that matches the fullName specified, excluding
     * posts, limiting the list size to the dimension specified
     *
     * @param fullNamePattern full name regex pattern, matches all full names that contains a prefix in any of its word
     * @param pageSize number of reporters to retrieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> reportersByFullName(String fullNamePattern, Integer pageSize) throws SocialNewsDataAccessException;

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
    List<Reporter> reportersByFullName(String fullNamePattern, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the reporters saved on database, excluding posts, limiting the list size to the
     * dimension specified
     *
     * @param pageSize number of reporters to retrieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> allReporters(Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the reporter saved on database, excluding posts, limiting the list size to the
     * dimension specified, starting from the reporter specified as argument. It allows the implementation of
     * pagination of the reporters
     *
     * @param offset reporter from which the query starts to retrieve information
     * @param pageSize ieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> allReporters(Reporter offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Remove a reporter from the database
     *
     * @param reporterId reporter identifier
     * @return number of reporter removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    Long removeReporter(String reporterId) throws SocialNewsDataAccessException;
}
