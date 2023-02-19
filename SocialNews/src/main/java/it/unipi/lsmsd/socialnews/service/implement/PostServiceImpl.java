package it.unipi.lsmsd.socialnews.service.implement;

import it.unipi.lsmsd.socialnews.dao.DAOLocator;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Comment;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dto.CommentDTO;
import it.unipi.lsmsd.socialnews.dto.PostDTO;
import it.unipi.lsmsd.socialnews.service.PostService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.Page;
import it.unipi.lsmsd.socialnews.service.util.Util;
import java.util.ArrayList;
import java.util.List;

public class PostServiceImpl implements PostService {

    private List<PostDTO> pagePostsByHashtag(String hashtag, PostDTO postOffset, Page page) throws SocialNewsServiceException {
        try {
            Post offset  = postOffset == null ? null:Util.buildPost(postOffset);
            List<PostDTO> pagePostDTO = new ArrayList<>();
            switch (page){
                case FIRST, NEXT -> DAOLocator.getPostDAO()
                        .postsByHashtagNext(hashtag, offset, Util.getIntProperty("listSearchPostsPageSize", 20))
                        .forEach(reporter -> reporter.getPosts()
                                .forEach(post -> pagePostDTO.add(Util.buildPostDTO(post, reporter.getReporterId()))));
                case PREV -> DAOLocator.getPostDAO()
                        .postsByHashtagPrev(hashtag, offset, Util.getIntProperty("listSearchPostsPageSize", 20))
                        .forEach(reporter -> reporter.getPosts()
                                .forEach(post -> pagePostDTO.add(Util.buildPostDTO(post, reporter.getReporterId()))));
            }

            return pagePostDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error: " + ex.getMessage());
        }
    }

    private List<CommentDTO> pageComments(String postId, CommentDTO commentOffset, Page page) throws SocialNewsServiceException{
        try {
            Comment offset  = commentOffset == null ? null:Util.buildComment(commentOffset);
            List<CommentDTO> pageCommentDTO = new ArrayList<>();

            switch (page){
                case FIRST, NEXT -> DAOLocator.getCommentDAO()
                        .commentsByPostIdNext(postId, offset,
                                Util.getIntProperty("listCommentPageSize",20))
                        .forEach(comment -> pageCommentDTO.add(Util.buildCommentDTO(comment)));
                case PREV -> DAOLocator.getCommentDAO()
                        .commentsByPostIdPrev(postId, offset,
                                Util.getIntProperty("listCommentPageSize",20))
                        .forEach(comment -> pageCommentDTO.add(Util.buildCommentDTO(comment)));
            }
            return pageCommentDTO;
        } catch (SocialNewsDataAccessException ex) {
            ex.printStackTrace();
            throw new SocialNewsServiceException("Database error: " + ex.getMessage());
        }
    }

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
    public List<PostDTO> firstPagePostsByHashtag(String hashtag) throws SocialNewsServiceException {
        return pagePostsByHashtag(hashtag, null, Page.FIRST);
    }

    @Override
    public List<PostDTO> prevPagePostsByHashtag(String hashtag, PostDTO postOffset) throws SocialNewsServiceException {
        return pagePostsByHashtag(hashtag, postOffset, Page.PREV);
    }

    @Override
    public List<PostDTO> nextPagePostsByHashtag(String hashtag, PostDTO postOffset) throws SocialNewsServiceException {
        return pagePostsByHashtag(hashtag, postOffset, Page.NEXT);
    }

    @Override
    public List<CommentDTO> firstPageComments(String postId) throws SocialNewsServiceException {
        return pageComments(postId, null, Page.FIRST);
    }

    @Override
    public List<CommentDTO> prevPageComments(String postId, CommentDTO commentOffset) throws SocialNewsServiceException {
        return pageComments(postId, commentOffset, Page.PREV);
    }

    @Override
    public List<CommentDTO> nextPageComments(String postId, CommentDTO commentOffset) throws SocialNewsServiceException {
        return pageComments(postId, commentOffset, Page.NEXT);
    }

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
