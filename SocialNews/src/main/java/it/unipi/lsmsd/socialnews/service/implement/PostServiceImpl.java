package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dto.CommentDTO;
import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.service.PostService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.ServiceWorkerPool;
import it.unipi.lsmsd.socialnews.service.util.Util;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PostServiceImpl implements PostService {
    /**
     * Publish a post on the page of the reporter who created the post
     *
     * @param newPost DTO object containing the information of the new post to publish
     * @return the identifier assigned to the new post
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public String publishPost(PostDTO newPost) throws SocialNewsServiceException {
        try {
            return DAOLocator.getPostDAO().createPost(newPost.getReporterId(), Util.buildPost(newPost));
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Missing some required fields");
        }
    }

    /**
     * Publish a comment related to a selected post
     *
     * @param newComment DTO object containing the information of the new comment to publish
     * @return the identifier assigned to the new comment
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    @Override
    public String publishComment(CommentDTO newComment) throws SocialNewsServiceException {
        try {
            return DAOLocator.getCommentDAO().createComment(Util.buildComment(newComment));
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Missing some required fields");
        }
    }

    @Override
    public List<PostDTO> searchPostsByHashtag(String hashtag) throws SocialNewsServiceException {
        throw new RuntimeException("Not yet implemented");//TODO
    }

    public List<CommentDTO> loadComments(PostDTO targetPost) throws SocialNewsServiceException {
        throw new RuntimeException("Not yet implemented");//TODO

    }

    /**
     * Remove a post from the system, removing the information stored in the database and all the associated comments
     *
     * @param toRemovePost DTO object containing the postId and reporterId of the comment to remove
     * @throws SocialNewsServiceException in case of failure of the operation or if the post is not in the system
     */
    @Override
    public void removePost(PostDTO toRemovePost) throws SocialNewsServiceException {
        try {
            List<Future<?>> removedPostCounter = ServiceWorkerPool.getPool().submitTask(List.of(
                    () -> DAOLocator.getPostDAO().removePost(toRemovePost.getReporterId(), toRemovePost.getId()),
                    () -> DAOLocator.getCommentDAO().removeCommentsByPostId(toRemovePost.getId())
            ));

            if ((Long) removedPostCounter.get(0).get() == 0){
                throw new SocialNewsServiceException("Post not in the system");
            }
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Parallel execution error: " + ex.getMessage());
        }
    }

    /**
     * Remove a comment from the system, removing the information stored in the database
     *
     * @param toRemoveComment DTO object containing the commentId of the comment to remove
     * @throws SocialNewsServiceException in case of failure of the operation or if the comment is not in the system
     */
    @Override
    public void removeComment(CommentDTO toRemoveComment) throws SocialNewsServiceException {
        try {
            Long removedCounter = DAOLocator.getCommentDAO().removeComment(toRemoveComment.getId());
            if (removedCounter == 0){
                throw new SocialNewsServiceException("Comment not in the system");
            }
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }
}
