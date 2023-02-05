package it.unipi.lsmsd.socialnews.dao.model;

import java.util.Date;
import java.util.UUID;

public class Comment extends BaseEntity {
    String id;
    Reader reader;
    String postId;
    String text;
    Date timestamp;

    public Comment(){
        id = UUID.randomUUID().toString();
        reader = new Reader();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public void setReader(String readerId, String readerFullName) {
        this.reader = new Reader(readerId, readerFullName);
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
        return "Comment{" +
                "id='" + id + '\'' +
                ", reader=" + reader +
                ", postId='" + postId + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public static class Reader{
        String id;
        String fullName;

        public Reader(){ }

        public Reader(String id, String fullName) {
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
            return "Reader{" +
                    "id='" + id + '\'' +
                    ", fullName='" + fullName + '\'' +
                    '}';
        }
    }

}

