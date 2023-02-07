package it.unipi.lsmsd.socialnews.dao.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Report extends BaseEntity {
    private String reportId;
    private String readerId;
    private String postId;
    private String timestamp;
    private String text;

    public Report() {
        this.reportId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getReportId() {
        return reportId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public String getReaderId() {
        return readerId;
    }

    public String getPostId() {
        return postId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", timestamp='" + timestamp + '\'' +
                ", text='" + text + '\'' +
                ", readerId=" + readerId +
                ", postId=" + postId +
                '}';
    }
}
