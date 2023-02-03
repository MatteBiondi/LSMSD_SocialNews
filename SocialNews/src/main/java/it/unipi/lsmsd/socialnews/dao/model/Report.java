package it.unipi.lsmsd.socialnews.dao.model;

import java.time.LocalDateTime;

public class Report {
    private Long reportId;
    private String readerId;
    private String postId;
    private LocalDateTime timestamp;
    private String text;

    public Report() {
    }

    public Report(Long reportId, String readerId, String postId, LocalDateTime timestamp, String text) {
        this.reportId = reportId;
        this.readerId = readerId;
        this.postId = postId;
        this.timestamp = timestamp;
        this.text = text;
    }

    public Long getReportId() {
        return reportId;
    }

    public LocalDateTime getTimestamp() {
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

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
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
                "id=" + reportId +
                ", timestamp='" + timestamp + '\'' +
                ", text='" + text + '\'' +
                ", reader=" + readerId +
                ", post=" + postId +
                '}';
    }
}
