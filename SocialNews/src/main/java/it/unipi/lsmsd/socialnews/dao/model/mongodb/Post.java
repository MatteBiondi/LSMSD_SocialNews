package it.unipi.lsmsd.socialnews.dao.model.mongodb;

import it.unipi.lsmsd.socialnews.dao.model.BaseEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Post extends BaseEntity {
    String postId;
    String text;
    Date timestamp;
    List<String> links;
    List<String> hashtags;
    Integer numOfComment;

    public Post(){
        this.postId = UUID.randomUUID().toString();
        this.timestamp = Calendar.getInstance().getTime();
    }

    public Post(String postId, String text, Date timestamp, List<String> links, List<String> hashtags, Integer numOfComment) {
        this.postId = postId;
        this.text = text;
        this.timestamp = timestamp;
        this.links = links;
        this.hashtags = hashtags;
        this.numOfComment = numOfComment;
    }

    public String getId() {
        return postId;
    }

    public void setId(String postId) {
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

    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", links=" + links +
                ", hashtags=" + hashtags +
                ", numOfComment=" + numOfComment +
                '}';
    }
}
