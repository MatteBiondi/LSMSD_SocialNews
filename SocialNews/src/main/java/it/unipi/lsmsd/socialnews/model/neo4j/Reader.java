package it.unipi.lsmsd.socialnews.model.neo4j;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;


@NodeEntity
public class Reader {
    @Id
    @Property(name="reader_id")
    private String readerId;

    @Relationship(type = "FOLLOW", direction = Relationship.Direction.OUTGOING)
    Set<Reporter> followingReporter;

    public Reader() {
    }

    public Reader(String readerId) {
        this.readerId = readerId;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public Set<Reporter> getFollowingReporter() {
        return followingReporter;
    }

    public void addFollowing(Reporter reporter) {
        if (followingReporter == null) {
            followingReporter = new HashSet<>();
        }
        followingReporter.add(reporter);
        reporter.addFollower(this);
    }

    @Override
    public String toString() {
        return "Reader{" +
                "readerId='" + readerId + '\'' +
                '}';
    }
}
