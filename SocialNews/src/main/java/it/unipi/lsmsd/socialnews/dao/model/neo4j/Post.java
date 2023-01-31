package it.unipi.lsmsd.socialnews.dao.model.neo4j;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Post {
    @Id
    @Property(name="post_id")
    private String postId;

    @Relationship(type = "REPORT", direction = Relationship.Direction.INCOMING)
    Set<Report> reports;

    @Relationship(type = "WRITE", direction = Relationship.Direction.INCOMING)
    Reporter reporter;

    public Post() {
    }

    public Post(String postId) {
        this.postId = postId;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public void addReport(Report report) {
        if (reports == null) {
            reports = new HashSet<>();
        }
        reports.add(report);
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", reporter=" + reporter +
                '}';
    }
}