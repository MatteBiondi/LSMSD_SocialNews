package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dto.CommentDTO;
import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;

public interface PostService {

    /**
     * Publish a post on the page of the reporter who created the post
     *
     * @param newPost DTO object containing the information of the new post to publish
     * @return the identifier assigned to the new post
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    String publishPost(PostDTO newPost) throws SocialNewsServiceException;

    /**
     * Publish a comment related to a selected post
     *
     * @param newComment DTO object containing the information of the new comment to publish
     * @return the identifier assigned to the new comment
     * @throws SocialNewsServiceException in case of failure of the operation
     */
    String publishComment(CommentDTO newComment) throws SocialNewsServiceException;

    /**
     * Remove a post from the system, removing the information stored in the database and all the associated comments
     *
     * @param toRemovePost DTO object containing the postId and reporterId of the comment to remove
     * @throws SocialNewsServiceException in case of failure of the operation or if the post is not in the system
     */
    void removePost(PostDTO toRemovePost) throws SocialNewsServiceException;

    /**
     * Remove a comment from the system, removing the information stored in the database
     *
     * @param toRemoveComment DTO object containing the commentId of the comment to remove
     * @throws SocialNewsServiceException in case of failure of the operation or if the comment is not in the system
     */
    void removeComment(CommentDTO toRemoveComment) throws SocialNewsServiceException;
}
