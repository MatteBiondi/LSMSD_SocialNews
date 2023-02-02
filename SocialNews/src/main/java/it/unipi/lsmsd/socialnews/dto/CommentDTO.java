package it.unipi.lsmsd.socialnews.dto;

import it.unipi.lsmsd.socialnews.dao.model.mongodb.Comment;

import java.util.Date;

public class CommentDTO extends BaseDTO{
    String id;
    Comment.Reader reader;
    String postId;
    String text;
    Date timestamp;

    public CommentDTO(){ }

    public CommentDTO(String id, Comment.Reader reader, String postId, String text, Date timestamp) {
        this.id = id;
        this.reader = reader;
        this.postId = postId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Comment.Reader getReader() {
        return reader;
    }

    public void setReader(Comment.Reader reader) {
        this.reader = reader;
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

    @Override
    public String toString() {
        return "CommentDTO{" +
                "id='" + id + '\'' +
                ", reader=" + reader +
                ", postId='" + postId + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
