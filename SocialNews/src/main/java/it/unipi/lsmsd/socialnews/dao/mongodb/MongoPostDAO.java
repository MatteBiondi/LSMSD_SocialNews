package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.model.*;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Post;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reporter;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;

public class MongoPostDAO extends MongoDAO<Reporter> {

    public MongoPostDAO(){
        super("reporters", Reporter.class);
    }

    public Long createPost(String reporterId, Post newPost) throws SocialNewsDataAccessException {
        try {
            return getCollection()
                    .updateOne(
                            Filters.and(
                                    Filters.eq("reporterId", reporterId),
                                    Filters.exists("email",true)),
                            Updates.combine(
                                    Updates.push("posts", newPost),
                                    Updates.inc("numOfPost",1)))//TODO: redundancy ?
                    .getModifiedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Insertion failed: " + me.getMessage());
        }
    }

    public List<Post> postsByReporterId(String reporterId, Integer pageSize) throws SocialNewsDataAccessException {
        return postsByReporterId(reporterId, null, pageSize);
    }

    public List<Post> postsByReporterId(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            List<Bson> stages = new ArrayList<>();

            stages.add(Aggregates.match(Filters.eq("reporterId", reporterId)));
            stages.add(Aggregates.unwind("$posts"));
            if(offset != null){
                stages.add(Aggregates.match(Filters.lt("posts.timestamp", offset.getTimestamp())));
            }
            stages.add(Aggregates.sort(Sorts.descending("posts.timestamp")));
            stages.add(Aggregates.limit(pageSize));
            stages.add(Aggregates.group("$reporterId",Accumulators.push("posts", "$posts")));

            Reporter reporter = getCollection().aggregate(stages).first();

            if(reporter == null)
                return new ArrayList<>();// Empty list
            else
                return reporter.getPosts();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public List<Reporter> postsByHashtag(String hashtag) throws SocialNewsDataAccessException {
        try{
            List<Reporter> reporters = new ArrayList<>();
            List<Bson> stages = new ArrayList<>();
            stages.add(Aggregates.match(Filters.eq("posts.hashtags", hashtag)));
            stages.add(Aggregates.project(Projections.fields(
                    Projections.include("reporterId"),
                    Projections.computed(
                    "posts",
                    Document.parse(String.format(
                            "{$filter:{input:'$posts',as:'p',cond:{$in:['%s','$$p.hashtags']}}}", hashtag))))
            ));
            stages.add(Aggregates.unwind("$posts"));
            stages.add(Aggregates.group("$reporterId",
                    List.of(
                            Accumulators.push("posts", "$posts"),
                            Accumulators.first("reporterId","$reporterId")))
            );
            getCollection().aggregate(stages).into(reporters);
            return reporters;
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public Long removePost(String reporterId, String postId) throws SocialNewsDataAccessException {
        try{
            return getCollection()
                    .updateOne(
                            Filters.eq("reporterId", reporterId),
                            Updates.pullByFilter(Filters.eq("postId", postId))
                    ).getModifiedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Deletion failed: " + me.getMessage());
        }
    }
}
