package it.unipi.lsmsd.socialnews.service;

import it.unipi.lsmsd.socialnews.dto.CommentDTO;
import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import java.util.List;

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
     * Retrieves all the posts that contains a hashtag passed as parameter, up to a configured number of posts
     *
     * @param hashtag hashtag to search in the posts
     * @return list of posts that contain the hashtag
     * @throws SocialNewsServiceException in case of failure of the query operation
     */
    List<PostDTO> firstPagePostsByHashtag(String hashtag) throws SocialNewsServiceException;

    /**
     * Retrieves all the posts that contains a hashtag passed as parameter starting from the offset passed as argument,
     * up to a configured number of posts
     *
     * @param hashtag hashtag to search in the posts
     * @param postOffset post DTO containing the id of the last post in the previous page
     * @return list of posts that contain the hashtag
     * @throws SocialNewsServiceException in case of failure of the query operation
     */
    List<PostDTO> nextPagePostsByHashtag(String hashtag, PostDTO postOffset) throws SocialNewsServiceException;

    /**
     * Retrieves the comments associated to the post specified as argument, ordered by timestamp, up to a configured
     * number of
     * comments
     *
     * @param targetPostId post whose comments to load
     * @return list of comments associated to the post
     * @throws SocialNewsServiceException in case of failure of the query operation
     */
    List<CommentDTO> firstPageComments(String targetPostId) throws SocialNewsServiceException;

    /**
     * Retrieves the comments associated to the post specified as argument ordered by timestamp starting from the offset
     * passed as argument, up to a configured number of comments
     *
     * @param targetPostId post whose comments to load
     * @param commentOffset comment DTO containing id the last comment in the previous page
     * @return list of comments associated to the post
     * @throws SocialNewsServiceException in case of failure of the query operation
     */
    List<CommentDTO> nextPageComments(String targetPostId, CommentDTO commentOffset) throws SocialNewsServiceException;

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
     * @param toRemoveCommentId identifier of the comment to remove
     * @throws SocialNewsServiceException in case of failure of the operation or if the comment is not in the system
     */
    void removeComment(String toRemoveCommentId) throws SocialNewsServiceException;
}
