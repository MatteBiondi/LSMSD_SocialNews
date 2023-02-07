package it.unipi.lsmsd.socialnews.dao.implement;

import it.unipi.lsmsd.socialnews.dao.PostDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoPostDAO;
import it.unipi.lsmsd.socialnews.dao.neo4j.Neo4jPostDAO;
import java.util.Date;
import java.util.List;

public class PostDAOImpl implements PostDAO {

    private final MongoPostDAO mongoPostDAO;
    private final Neo4jPostDAO neo4jPostDAO;

    public PostDAOImpl(){
        mongoPostDAO = new MongoPostDAO();
        neo4jPostDAO = new Neo4jPostDAO();
    }

    /**
     * Insert new post into the database
     *
     * @param reporterId reporter identifier
     * @param newPost    post object containing information of the new post
     * @return identifier assigned to the new post
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
    @Override
    public String createPost(String reporterId, Post newPost) throws SocialNewsDataAccessException {
        // Insert on Neo4J is lazy
        Long modifiedDocs = mongoPostDAO.createPost(reporterId, newPost);
        if (modifiedDocs > 0)
            return newPost.getId();
        else
            throw new SocialNewsDataAccessException("Insertion failed");
    }

    /**
     * Retrieves information about all the posts saved on database associated to the reporterId specified, limiting
     * the list size to the dimension specified
     *
     * @param reporterId reporter identifier used to filter the posts
     * @param pageSize   number of posts to retrieve
     * @return list of post objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Post> postsByReporterId(String reporterId, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoPostDAO.postsByReporterId(reporterId, pageSize);
    }

    /**
     * Retrieves information about all the posts saved on database associated to the reporterId, limiting the
     * list size to the dimension specified, starting from the post specified as argument. It allows the
     * implementation of pagination of the posts
     *
     * @param reporterId reporter identifier
     * @param offset     post from which the query starts to retrieve information
     * @param pageSize   number of posts to retrieve
     * @return list of post objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Post> postsByReporterId(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoPostDAO.postsByReporterId(reporterId, offset, pageSize);
    }

    /**
     * Retrieves the posts, along with basic information of the reporter associated, that contain the specified
     * hashtag, limiting the number of posts to the dimension specified
     *
     * @param hashtag  hashtag used to filter the posts
     * @param pageSize number of posts to retrieve
     * @return list of reporters objects containing basic information of the reporter and the list of posts that
     * contain the hashtag
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reporter> postsByHashtag(String hashtag, Integer pageSize) throws SocialNewsDataAccessException {
        return postsByHashtag(hashtag, null, pageSize);
    }

    /**
     * Retrieves all the posts, along with basic information of the reporter associated, that contain the specified
     * hashtag
     *
     * @param hashtag hashtag used to filter the posts
     * @return list of reporters objects containing basic information of the reporter and the list of posts that
     * contain the hashtag
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reporter> postsByHashtag(String hashtag, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoPostDAO.postsByHashtag(hashtag, offset, pageSize);
    }

    /**
     * Remove a post from the system
     *
     * @param reporterId reporter identifier
     * @param postId     post identifier
     * @return number of posts removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    @Override
    public Long removePost(String reporterId, String postId) throws SocialNewsDataAccessException {
        // todo : transaction
        neo4jPostDAO.deletePost(postId);
        return mongoPostDAO.removePost(reporterId, postId);
    }

    /**
     * Retrieves the top N most commented posts of a given reporter, starting from a given instant
     *
     * @param reporterId reporter identifier
     * @param nTop       top N posts returned by the query
     * @param from       instant from which starts the computation
     * @return list of post objects containing the information about the top N posts of the reporter specified
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Post> latestHottestPosts(String reporterId, Integer nTop, Date from) throws SocialNewsDataAccessException {
        return mongoPostDAO.latestHottestPosts(reporterId, nTop, from);
    }
}
