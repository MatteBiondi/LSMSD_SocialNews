package it.unipi.lsmsd.socialnews.dto;

import java.util.Date;

public class CommentDTO extends BaseDTO{
    String id;
    ReaderDTO reader;
    String postId;
    String text;
    Date timestamp;

    public CommentDTO(){ }

    public CommentDTO(String readerId, String readerFullName, String postId, String text) {
        this.reader = new ReaderDTO(readerId, readerFullName);
        this.postId = postId;
        this.text = text;
        this.timestamp = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReaderDTO getReader() {
        return reader;
    }

    public void setReader(ReaderDTO reader) {
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

    public static class ReaderDTO{
        String id;
        String fullName;

        public ReaderDTO(){ }

        public ReaderDTO(String id, String fullName) {
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
}
