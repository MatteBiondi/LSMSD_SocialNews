package it.unipi.lsmsd.socialnews.dto;

import java.util.Date;

public class ReportDTO extends BaseDTO{
    private String reportId;
    private String readerId;
    private String postId;
    private Date timestamp;
    private String text;

    public ReportDTO() {
    }

    public ReportDTO(String readerId, String postId, String text) {
        this.readerId = readerId;
        this.postId = postId;
        this.timestamp = new Date();
        this.text = text;
    }

    public String getReportId() {
        return reportId;
    }

    public Date getTimestamp() {
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

    public void setTimestamp(Date timestamp) {
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
        return "ReportDTO{" +
                "reportId=" + reportId +
                ", timestamp='" + timestamp + '\'' +
                ", text='" + text + '\'' +
                ", readerId=" + readerId +
                ", postId=" + postId +
                '}';
    }
}
