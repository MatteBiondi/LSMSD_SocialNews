package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
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
     * Retrieves information about all the posts saved on database associated to the reporterId specified, limiting
     * the list size to the dimension specified
     *
     * @param reporterId reporter identifier used to filter the posts
     * @param pageSize number of posts to retrieve
     * @return list of post objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Post> postsByReporterId(String reporterId, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves information about all the posts saved on database associated to the reporterId, limiting the
     * list size to the dimension specified, starting from the post specified as argument. It allows the
     * implementation of pagination of the posts
     * @param reporterId reporter identifier
     * @param offset post from which the query starts to retrieve information
     * @param pageSize number of posts to retrieve
     * @return list of post objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Post> postsByReporterId(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException;

    /**
     * Retrieves all the posts, along with basic information of the reporter associated, that contain the specified
     * hashtag
     *
     * @param hashtag hashtag used to filter the posts
     * @return list of reporters objects containing basic information of the reporter and the list of posts that
     * contain the hashtag
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    List<Reporter> postsByHashtag(String hashtag) throws SocialNewsDataAccessException;//TODO: pagination ?

    /**
     *
     * @param reporterId reporter identifier
     * @param postId post identifier
     * @return number of posts removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    Long removePost(String reporterId, String postId) throws SocialNewsDataAccessException;
}
