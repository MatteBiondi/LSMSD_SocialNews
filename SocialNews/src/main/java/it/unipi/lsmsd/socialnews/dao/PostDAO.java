package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Post;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reporter;
import java.util.List;

public interface PostDAO {
    Long createPost(String reporterId, Post newPost) throws SocialNewsDataAccessException;
    List<Post> postsByReporterId(String reporterId, Integer pageSize) throws SocialNewsDataAccessException;
    List<Post> postsByReporterId(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException;
    List<Reporter> postsByHashtag(String hashtag) throws SocialNewsDataAccessException;//TODO: pagination ?
    Long removePost(String reporterId, String postId) throws SocialNewsDataAccessException;
}
