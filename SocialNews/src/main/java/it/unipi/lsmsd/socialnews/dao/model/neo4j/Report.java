package it.unipi.lsmsd.socialnews.dao.model.neo4j;

import org.neo4j.ogm.annotation.*;


@RelationshipEntity(type="REPORT")
public class Report {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Reader reader;

    @EndNode
    private Post post;

    @Property(name="timestamp")
    //fixme localdatetime
    private String timestamp;

    @Property(name="text")
    private String text;

    public Report() {
    }

    public Report(String timestamp, String text, Reader reader, Post post) {
        this.timestamp = timestamp;
        this.text = text;
        this.reader = reader;
        this.post = post;
    }

    public Long getId() {
        return id;
    }

    public String getTimestamp() {
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public void setPost(Post post) {
        this.post = post;
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
