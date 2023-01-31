package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Comment;
import java.util.List;

public interface CommentDAO {
    String createComment(Comment newComment) throws SocialNewsDataAccessException;
    List<Comment> commentsByPostId(String postId, Integer pageSize) throws SocialNewsDataAccessException;
    List<Comment> commentsByPostId(String postId, Comment offset, Integer pageSize) throws SocialNewsDataAccessException;
    Long removeCommentsByPostId(String postId) throws SocialNewsDataAccessException;
    Long removeComment(String commentId) throws SocialNewsDataAccessException;
}
