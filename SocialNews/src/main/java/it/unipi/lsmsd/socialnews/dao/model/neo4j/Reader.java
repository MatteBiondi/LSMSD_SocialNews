package it.unipi.lsmsd.socialnews.dao.model.neo4j;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;


@NodeEntity
public class Reader {
    @Id
    @Property(name="reader_id")
    private String readerId;

    //fixme
    //@Relationship(type = "FOLLOW", direction = Relationship.Direction.OUTGOING)
    //Set<Reporter> followingReporter;

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

    /*fixme
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

    public void removeFollowing(Reporter reporter) {
        if (followingReporter != null && !followingReporter.isEmpty()) {
            followingReporter.remove(reporter);
        }
        reporter.removeFollower(this);
    }
     */

    @Override
    public String toString() {
        return "Reader{" +
                "readerId='" + readerId + '\'' +
                '}';
    }
}
