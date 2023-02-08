package it.unipi.lsmsd.socialnews.dto;

import java.util.Date;

public class CommentDTO extends BaseDTO{
    String id;
    InnerReaderDTO reader;
    InnerPostDTO post;
    String text;
    Date timestamp;

    public CommentDTO(){ }

    public CommentDTO(String readerId, String readerFullName, String postId, String reporterId, String text) {
        this.reader = new InnerReaderDTO(readerId, readerFullName);
        this.post = new InnerPostDTO(postId, reporterId);
        this.text = text;
        this.timestamp = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InnerReaderDTO getReader() {
        return reader;
    }

    public void setReader(InnerReaderDTO reader) {
        this.reader = reader;
    }

    public InnerPostDTO getPost() {
        return post;
    }

    public void setPost(InnerPostDTO post) {
        this.post = post;
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

    public static class InnerReaderDTO{
        String id;
        String fullName;

        public InnerReaderDTO(){ }

        public InnerReaderDTO(String id, String fullName) {
            this.id = id;
            this.fullName = fullName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        @Override
        public String toString() {
            return "ReaderDTO{" +
                    "id='" + id + '\'' +
                    ", fullName='" + fullName + '\'' +
                    '}';
        }
    }

    public static class InnerPostDTO{
        String id;
        String reporterId;

        public InnerPostDTO(){ }

        public InnerPostDTO(String id, String reporterId) {
            this.id = id;
            this.reporterId = reporterId;
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

        @Override
        public String toString() {
            return "InnerPostDTO{" +
                    "id='" + id + '\'' +
                    ", reporterId='" + reporterId + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "id='" + id + '\'' +
                ", reader=" + reader +
                ", post=" + post +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
