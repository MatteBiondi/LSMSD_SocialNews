package it.unipi.lsmsd.socialnews.dao.model.neo4j;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;


@NodeEntity
public class Post {
    @Id
    @Property(name="post_id")
    private String postId;

    @Relationship(type = "WRITE", direction = Relationship.Direction.INCOMING)
    Reporter reporter;

    public Post() {
    }

    public Post(String postId) {
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
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

    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", reporter=" + reporter +
                '}';
    }
}
