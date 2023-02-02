package it.unipi.lsmsd.socialnews.dto;

import java.util.Date;
import java.util.List;

public class PostDTO extends BaseDTO{
    String postId;
    String text;
    Date timestamp;
    List<String> links;
    List<String> hashtags;
    Integer numOfComment; //TODO: necessary ?
    List<CommentDTO> comments;

    public PostDTO(){ }

    public PostDTO(String postId, String text, Date timestamp, List<String> links, List<String> hashtags, Integer numOfComment, List<CommentDTO> comments) {
        this.postId = postId;
        this.text = text;
        this.timestamp = timestamp;
        this.links = links;
        this.hashtags = hashtags;
        this.numOfComment = numOfComment;
        this.comments = comments;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public Integer getNumOfComment() {
        return numOfComment;
    }

    public void setNumOfComment(Integer numOfComment) {
        this.numOfComment = numOfComment;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "postId='" + postId + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", links=" + links +
                ", hashtags=" + hashtags +
                ", numOfComment=" + numOfComment +
                ", comments=" + comments +
                '}';
    }
}
