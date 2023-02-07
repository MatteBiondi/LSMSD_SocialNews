package it.unipi.lsmsd.socialnews.dao.implement;

import com.fasterxml.jackson.databind.node.ArrayNode;
import it.unipi.lsmsd.socialnews.dao.CommentDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Comment;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoCommentDAO;
import java.util.Date;
import java.util.List;

public class CommentDAOImpl implements CommentDAO {

    private final MongoCommentDAO mongoCommentDAO;

    public CommentDAOImpl(){
        mongoCommentDAO = new MongoCommentDAO();
    }

    /**
     * Insert new comment into the database
     *
     * @param newComment comment object containing information of the new comment to save on database
     * @return identifier assigned to the new comment
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
    @Override
    public String createComment(Comment newComment) throws SocialNewsDataAccessException {
        return mongoCommentDAO.createComment(newComment);
    }

    /**
     * Retrieves information about all the comments saved on database associated to the postId, limiting
     * the list size to the dimension specified
     *
     * @param postId   post identifier used to filter the comments
     * @param pageSize number of comments to retrieve
     * @return list of comment objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Comment> commentsByPostId(String postId, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoCommentDAO.commentsByPostId(postId, pageSize);
    }

    /**
     * Retrieves information about all the comments saved on database associated to the postId, limiting the
     * list size to the dimension specified, starting from the comment specified as argument. It allows the
     * implementation of pagination of the comments
     *
     * @param postId   post identifier used to filter the comments
     * @param offset   comment from which the query starts to retrieve information
     * @param pageSize number of comments to retrieve
     * @return list of comment objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Comment> commentsByPostId(String postId, Comment offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoCommentDAO.commentsByPostId(postId, offset, pageSize);
    }

    /**
     * Remove all comments associated to the postId specified as argument
     *
     * @param postId post identifier used to filter the comments
     * @return number of comments removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    @Override
    public Long removeCommentsByPostId(String postId) throws SocialNewsDataAccessException {
        return mongoCommentDAO.removeCommentsByPostId(postId);
    }

    /**
     * Remove a comment from the database
     *
     * @param commentId comment identifier
     * @return number of comments removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    @Override
    public Long removeComment(String commentId) throws SocialNewsDataAccessException {
        return mongoCommentDAO.removeComment(commentId);
    }

    /**
     * Computes the top N most active readers in the system, ranked by the number of comments written, starting from
     * a given instant
     *
     * @param topN top N readers returned by the query
     * @param from instant from which starts the computation
     * @return JSON array containing the information computed by aggregation pipeline
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    @Override
    public ArrayNode latestMostActiveReaders(Integer topN, Date from) throws SocialNewsDataAccessException {
        return mongoCommentDAO.latestMostActiveReaders(topN, from);
    }

    /**
     * Computes the number of comments in each time window of the day, starting from a given instant
     *
     * @param windowSize size of temporal window, must be a divisor of 24
     * @param from       instant from which starts the computation
     * @return JSON array containing the information computed by aggregation pipeline
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    @Override
    public ArrayNode latestHottestMomentsOfDay(Integer windowSize, Date from) throws SocialNewsDataAccessException {
        return mongoCommentDAO.latestHottestMomentsOfDay(windowSize, from);
    }
}
