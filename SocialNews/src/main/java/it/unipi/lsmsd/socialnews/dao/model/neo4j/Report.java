package it.unipi.lsmsd.socialnews.dao.model.neo4j;

import org.neo4j.ogm.annotation.*;

import java.time.LocalDateTime;

@RelationshipEntity(type="REPORT")
public class Report {
    @Id @GeneratedValue
    private Long id;

    @Property(name="timestamp")
    private LocalDateTime timestamp;

    @Property(name="text")
    private String text;

    @StartNode
    private Reader reader;

    @EndNode
    private Post post;

    public Report() {
    }

    public Report(LocalDateTime timestamp, String text, Reader reader, Post post) {
        this.timestamp = timestamp;
        this.text = text;
        this.reader = reader;
        this.post = post;

        post.addReport(this);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public Reader getReader() {
        return reader;
    }

    public Post getPost() {
        return post;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", timestamp='" + timestamp + '\'' +
                ", text='" + text + '\'' +
                ", reader=" + reader +
                ", post=" + post +
                '}';
    }
}
