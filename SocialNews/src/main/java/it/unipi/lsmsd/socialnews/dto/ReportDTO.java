package it.unipi.lsmsd.socialnews.dto;

public class ReportDTO extends BaseDTO{
    private Long reportId;
    private String readerId;
    private String postId;
    private String timestamp;
    private String text;

    public ReportDTO() {
    }

    public ReportDTO(String readerId, String postId, String timestamp, String text) {
        this.readerId = readerId;
        this.postId = postId;
        this.timestamp = timestamp;
        this.text = text;
    }

    public Long getReportId() {
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

    public void setReportId(Long reportId) {
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
        return "ReportDTO{" +
                "reportId=" + reportId +
                ", timestamp='" + timestamp + '\'' +
                ", text='" + text + '\'' +
                ", readerId=" + readerId +
                ", postId=" + postId +
                '}';
    }
}
