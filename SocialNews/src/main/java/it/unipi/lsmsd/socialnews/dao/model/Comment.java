package it.unipi.lsmsd.socialnews.dao.model;

import java.util.Date;
import java.util.UUID;

public class Comment extends BaseEntity {
    String id;
    InnerReader reader;
    InnerPost post;
    String text;
    Date timestamp;

    public Comment(){
        id = UUID.randomUUID().toString();
        reader = new InnerReader();
        post = new InnerPost();
        timestamp = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InnerReader getReader() {
        return reader;
    }

    public void setReader(InnerReader reader) {
        this.reader = reader;
    }

    public InnerPost getPost() {
        return post;
    }

    public void setPost(InnerPost post) {
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

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", reader=" + reader +
                ", post=" + post +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public static class InnerReader{
        String id;
        String fullName;

        public InnerReader(){ }

        public InnerReader(String id, String fullName) {
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

    public static class InnerPost {
        String id;
        String reporterId;

        public InnerPost(){ }

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
            return "Post{" +
                    "postId='" + id + '\'' +
                    ", reporterId='" + reporterId + '\'' +
                    '}';
        }
    }
}

