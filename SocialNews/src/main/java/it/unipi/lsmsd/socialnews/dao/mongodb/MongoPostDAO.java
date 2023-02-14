package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.*;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.Date;
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
                                    Updates.push("posts", newPost))
                    .getModifiedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Insertion failed: " + me.getMessage());
        }
    }

    public List<Post> postsByReporterIdPrev(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            List<Bson> stages = new ArrayList<>();

            stages.add(Aggregates.match(Filters.eq("reporterId", reporterId)));
            stages.add(Aggregates.unwind("$posts"));
            if(offset != null){
                stages.add(Aggregates.match(
                        Filters.or(
                                Filters.and(
                                        Filters.gte("posts.timestamp", offset.getTimestamp()),
                                        Filters.gt("posts._id", offset.getId())
                                ),
                                Filters.gt("posts.timestamp", offset.getTimestamp()))
                ));
            }
            stages.add(Aggregates.sort(Sorts.ascending("posts.timestamp", "posts._id")));
            stages.add(Aggregates.limit(pageSize));
            stages.add(Aggregates.sort(Sorts.descending("posts.timestamp", "posts._id")));
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

    public List<Post> postsByReporterIdNext(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            List<Bson> stages = new ArrayList<>();

            stages.add(Aggregates.match(Filters.eq("reporterId", reporterId)));
            stages.add(Aggregates.unwind("$posts"));
            if(offset != null){
                stages.add(Aggregates.match(
                        Filters.or(
                                Filters.and(
                                        Filters.lte("posts.timestamp", offset.getTimestamp()),
                                        Filters.lt("posts._id", offset.getId())
                                ),
                                Filters.lt("posts.timestamp", offset.getTimestamp()))
                ));
            }
            stages.add(Aggregates.sort(Sorts.descending("posts.timestamp", "posts._id")));
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

    public List<Reporter> postsByHashtagPrev(String hashtag, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
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
            if(offset != null){
                stages.add(Aggregates.match(
                        Filters.or(
                                Filters.and(
                                        Filters.gte("posts.timestamp", offset.getTimestamp()),
                                        Filters.gt("posts._id", offset.getId())
                                ),
                                Filters.gt("posts.timestamp", offset.getTimestamp()))
                ));
            }
            stages.add(Aggregates.sort(Sorts.ascending("posts.timestamp", "posts._id")));
            stages.add(Aggregates.limit(pageSize));
            stages.add(Aggregates.sort(Sorts.descending("posts.timestamp", "posts._id")));
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

    public List<Reporter> postsByHashtagNext(String hashtag, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
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
            if(offset != null){
                stages.add(Aggregates.match(
                        Filters.or(
                                Filters.and(
                                        Filters.lte("posts.timestamp", offset.getTimestamp()),
                                        Filters.lt("posts._id", offset.getId())
                                ),
                                Filters.lt("posts.timestamp", offset.getTimestamp()))
                        ));
            }
            stages.add(Aggregates.sort(Sorts.descending("posts.timestamp", "posts._id")));
            stages.add(Aggregates.limit(pageSize));
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

    public Long removePost(ClientSession session, String reporterId, String postId) throws SocialNewsDataAccessException {
        try{
            // Remove the post
            Long postRemoved = getCollection()
                    .updateOne(
                            session,
                            Filters.eq("reporterId", reporterId),
                            Updates.pullByFilter(Document.parse(String.format("{posts:{_id:'%s'}}", postId))))
                    .getModifiedCount();

            // Removes all associated comments
            getRawCollection("comments").deleteMany(session, Filters.eq("post._id", postId));

            return postRemoved;
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Deletion failed: " + me.getMessage());
        }
    }

    public List<Post> latestHottestPosts(String reporterId, Integer nTop, Date from)
            throws SocialNewsDataAccessException{
        try{
            List<Bson> stages = new ArrayList<>();

            stages.add(Aggregates.match(Filters.eq("reporterId", reporterId)));
            stages.add(Aggregates.project(Projections.fields(
                        Projections.exclude("_id"),
                        Projections.include("posts", "reporterId"))));
            stages.add(Aggregates.unwind("$posts"));
            stages.add(Aggregates.match(Filters.and(
                    Filters.gte("posts.timestamp", from),
                    Filters.exists("posts.numOfComment", true))));
            stages.add(Aggregates.sort(Sorts.descending("posts.numOfComment", "posts.timestamp")));
            stages.add(Aggregates.limit(nTop));
            stages.add(Aggregates.group("$reporterId",
                    Accumulators.first("reporterId","$reporterId"),
                    Accumulators.push("posts","$posts")));

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
}
