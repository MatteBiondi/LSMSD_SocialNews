package it.unipi.lsmsd.socialnews.dto;

import java.util.Date;
import java.util.List;

public class PostDTO extends BaseDTO{
    String id;
    String reporterId;
    String text;
    Date timestamp;
    List<String> links;
    List<String> hashtags;

    public PostDTO(){ }

    public PostDTO(String reporterId, String text, List<String> links, List<String> hashtags) {
        this.reporterId = reporterId;
        this.text = text;
        this.timestamp = new Date();
        this.links = links;
        this.hashtags = hashtags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
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

    @Override
    public String toString() {
        return "PostDTO{" +
                "id='" + id + '\'' +
                ", reporterId='" + reporterId + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", links=" + links +
                ", hashtags=" + hashtags +
                '}';
    }
}
