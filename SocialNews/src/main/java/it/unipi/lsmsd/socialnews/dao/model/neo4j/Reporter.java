package it.unipi.lsmsd.socialnews.dao.model.neo4j;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class Reporter {
    @Id
    @Property(name="reporter_id")
    private String reporterId;

    @Property(name="name")
    private String name;

    @Property(name="picture")
    private String picture;

    /* fixme
    @Relationship(type = "WRITE", direction = Relationship.Direction.OUTGOING)
    Set<Post> posts;

    @Relationship(type = "FOLLOW", direction = Relationship.Direction.INCOMING)
    Set<Reader> followers;
     */

    public Reporter() {
    }

    public Reporter(String reporterId, String name, String picture) {
        this.reporterId = reporterId;
        this.name = name;
        this.picture = picture;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    /* fixme
    public Set<Post> getPosts() {
        return posts;
    }

    public void addPost(Post post) {
        if (posts == null) {
            posts = new HashSet<>();
        }
        posts.add(post);
    }

    public void removePost(Post post){
        if(posts!=null && !posts.isEmpty()){
            posts.remove(post);
        }
    }

    public Set<Reader> getFollowers() {
        return followers;
    }

    public void addFollower(Reader reader) {
        if (followers == null) {
            followers = new HashSet<>();
        }
        followers.add(reader);
    }

    public void removeFollower(Reader reader) {
        if(followers != null && !followers.isEmpty()){
            followers.remove(reader);
        }
    }
     */

    @Override
    public String toString() {
        return "Reporter{" +
                "reporterId='" + reporterId + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}
