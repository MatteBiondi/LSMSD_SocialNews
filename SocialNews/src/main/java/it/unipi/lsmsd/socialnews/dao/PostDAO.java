package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import java.util.Date;
import java.util.List;

public interface PostDAO {
    /**
     * Insert new post into the database
     *
     * @param reporterId reporter identifier
     * @param newPost post object containing information of the new post
     * @return identifier assigned to the new post
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
    String createPost(String reporterId, Post newPost) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the posts saved on database associated to the reporterId, limiting the
     * list size to the dimension specified, starting from the post specified as argument in reverse order It allows the
     * implementation of pagination of the posts
     *
     * @param reporterId reporter identifier used to filter the posts
     * @param offset post from which the query starts to retrieve information
     * @param pageSize number of posts to retrieve
     * @return list of post objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Post> postsByReporterIdPrev(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the posts saved on database associated to the reporterId, limiting the
     * list size to the dimension specified, starting from the post specified as argument. It allows the
     * implementation of pagination of the posts
     *
     * @param reporterId reporter identifier
     * @param offset post from which the query starts to retrieve information
     * @param pageSize number of posts to retrieve
     * @return list of post objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Post> postsByReporterIdNext(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves the posts, along with basic information of the reporter associated, that contain the specified
     * hashtag, limiting the number of posts to the dimension specified
     *
     * @param hashtag hashtag used to filter the posts
     * @param pageSize number of posts to retrieve
     * @return list of reporters objects containing basic information of the reporter and the list of posts that
     * contain the hashtag
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> postsByHashtagPrev(String hashtag, Post offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves the posts, along with basic information of the reporter associated, that contain the specified
     * hashtag, limiting the number of posts to the dimension specified, starting from the post specified as argument
     *
     * @param hashtag hashtag used to filter the posts
     * @param offset post from which the query starts to retrieve information
     * @param pageSize number of posts to retrieve
     * @return list of reporters objects containing basic information of the reporter and the list of posts that
     * contain the hashtag
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> postsByHashtagNext(String hashtag, Post offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Remove a post and associated comments from the system
     *
     * @param reporterId reporter identifier
     * @param postId post identifier
     * @return number of posts removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    Long removePost(String reporterId, String postId) throws SocialNewsDataAccessException;

    /**
     * Retrieves the top N most commented posts of a given reporter, starting from a given instant
     *
     * @param reporterId reporter identifier
     * @param nTop top N posts returned by the query
     * @param from instant from which starts the computation
     * @return list of post objects containing the information about the top N posts of the reporter specified
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Post> latestHottestPosts(String reporterId, Integer nTop, Date from) throws SocialNewsDataAccessException;
}
