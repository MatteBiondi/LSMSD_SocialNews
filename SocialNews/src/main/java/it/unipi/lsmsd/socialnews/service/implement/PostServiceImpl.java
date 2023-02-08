package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Comment;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dto.CommentDTO;
import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.service.PostService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Util;
import java.util.ArrayList;
import java.util.List;

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
            if(newPost.getText().length() > Util.getIntProperty("maxPostLength", 5000)){
                throw new SocialNewsServiceException("Post text too long");
            }
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

    /**
     * Retrieves all the posts that contains a hashtag passed as parameter, up to a configured number of posts
     *
     * @param hashtag hashtag to search in the posts
     * @return list of posts that contain the hashtag
     * @throws SocialNewsServiceException in case of failure of the query operation
     */
    @Override
    public List<PostDTO> firstPagePostsByHashtag(String hashtag) throws SocialNewsServiceException {
        return nextPagePostsByHashtag(hashtag, null);
    }

    /**
     * Retrieves all the posts that contains a hashtag passed as parameter starting from the offset passed as argument,
     * up to a configured number of posts
     *
     * @param hashtag    hashtag to search in the posts
     * @param postOffset post DTO containing the id of the last post in the previous page
     * @return list of posts that contain the hashtag
     * @throws SocialNewsServiceException in case of failure of the query operation
     */
    @Override
    public List<PostDTO> nextPagePostsByHashtag(String hashtag, PostDTO postOffset) throws SocialNewsServiceException {
        try {
            Post offset  = postOffset == null ? null:Util.buildPost(postOffset);
            List<PostDTO> pagePostDTO = new ArrayList<>();
            DAOLocator.getPostDAO()
                    .postsByHashtag(hashtag, offset, Util.getIntProperty("listSearchPostsPageSize", 20))
                    .forEach(reporter -> reporter.getPosts()
                            .forEach(post -> pagePostDTO.add(Util.buildPostDTO(post, reporter.getReporterId()))));
            return pagePostDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }


    /**
     * Retrieves the associated to the post specified as argument, ordered by timestamp, up to a configured number of
     * comments
     *
     * @param targetPostId post whose comments to load
     * @return list of comments associated to the post
     * @throws SocialNewsServiceException in case of failure of the query operation
     */
    @Override
    public List<CommentDTO> firstPageComments(String targetPostId) throws SocialNewsServiceException {
        return nextPageComments(targetPostId, null);
    }

    /**
     * Retrieves the comments associated to the post specified as argument ordered by timestamp starting from the offset
     * passed as argument, up to a configured number of comments
     *
     * @param targetPostId post whose comments to load
     * @param commentOffset comment DTO containing id the last comment in the previous page
     * @return list of comments associated to the post
     * @throws SocialNewsServiceException in case of failure of the query operation
     */
    @Override
    public List<CommentDTO> nextPageComments(String targetPostId, CommentDTO commentOffset) throws SocialNewsServiceException {
        try {
            Comment offset  = commentOffset == null ? null:Util.buildComment(commentOffset);
            List<CommentDTO> pageCommentDTO = new ArrayList<>();
            DAOLocator.getCommentDAO()
                    .commentsByPostId(targetPostId, offset,
                            Util.getIntProperty("listCommentPageSize",20))
                    .forEach(comment -> pageCommentDTO.add(Util.buildCommentDTO(comment)));
            return pageCommentDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    /**
     * Remove a post from the system, removing the information stored in the database and all the associated comments
     *
     * @param toRemovePostId post identifier of the post to remove
     * @param reporterId reporter identifier of the report that published the post to remove
     * @throws SocialNewsServiceException in case of failure of the operation or if the post is not in the system
     */
    @Override
    public void removePost(String toRemovePostId, String reporterId) throws SocialNewsServiceException {
        try {
            Long removedPostCounter = DAOLocator.getPostDAO().removePost(reporterId, toRemovePostId);
            if (removedPostCounter == 0){
                throw new SocialNewsServiceException("Post not in the system");
            }
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }

    /**
     * Remove a comment from the system, removing the information stored in the database
     *
     * @param toRemoveCommentId identifier of the comment to remove
     * @throws SocialNewsServiceException in case of failure of the operation or if the comment is not in the system
     */
    @Override
    public void removeComment(String toRemoveCommentId) throws SocialNewsServiceException {
        try {
            Long removedCounter = DAOLocator.getCommentDAO().removeComment(toRemoveCommentId);
            if (removedCounter == 0){
                throw new SocialNewsServiceException("Comment not in the system");
            }
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error");
        }
    }
}
