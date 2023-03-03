package it.unipi.lsmsd.socialnews.dao.implement;

import com.fasterxml.jackson.databind.node.ArrayNode;
import it.unipi.lsmsd.socialnews.dao.CommentDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Comment;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoCommentDAO;
import it.unipi.lsmsd.socialnews.dao.redundancy.RedundancyTask;
import it.unipi.lsmsd.socialnews.dao.redundancy.RedundancyUpdater;
import it.unipi.lsmsd.socialnews.dao.redundancy.TaskType;
import it.unipi.lsmsd.socialnews.threading.ServiceWorkerPool;

import java.util.Date;
import java.util.List;

public class CommentDAOImpl implements CommentDAO {

    private final MongoCommentDAO mongoCommentDAO;

    public CommentDAOImpl(){
        mongoCommentDAO = new MongoCommentDAO();
    }

    @Override
    public String createComment(Comment newComment) throws SocialNewsDataAccessException {
        RedundancyTask task = new RedundancyTask(TaskType.ADD_COMMENT, newComment.getPost().getId());
        ServiceWorkerPool.getPool().submitTask(() -> RedundancyUpdater.getInstance().addTask(task));
        return mongoCommentDAO.createComment(newComment);
    }

    @Override
    public List<Comment> commentsByPostIdPrev(String postId, Comment offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoCommentDAO.commentsByPostIdPrev(postId, offset, pageSize);
    }

    @Override
    public List<Comment> commentsByPostIdNext(String postId, Comment offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoCommentDAO.commentsByPostIdNext(postId, offset, pageSize);
    }

    @Override
    public Long removeComment(String commentId, String postId) throws SocialNewsDataAccessException {
        RedundancyTask task = new RedundancyTask(TaskType.REMOVE_COMMENT, postId);
        ServiceWorkerPool.getPool().submitTask(() -> RedundancyUpdater.getInstance().addTask(task));
        return mongoCommentDAO.removeComment(commentId);
    }

    @Override
    public ArrayNode latestMostActiveReaders(Integer topN, Date from) throws SocialNewsDataAccessException {
        return mongoCommentDAO.latestMostActiveReaders(topN, from);
    }

    @Override
    public ArrayNode latestHottestMomentsOfDay(Integer windowSize, Date from) throws SocialNewsDataAccessException {
        return mongoCommentDAO.latestHottestMomentsOfDay(windowSize, from);
    }
}
