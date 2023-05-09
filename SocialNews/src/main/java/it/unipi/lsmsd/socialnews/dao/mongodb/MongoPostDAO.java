package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

    public Post postByPostId(String reporterId, String postId) throws SocialNewsDataAccessException{
        try{
            Reporter reporter =
                    getCollection()
                            .find(Filters.and(
                                    Filters.eq("reporterId", reporterId),
                                    Filters.elemMatch("posts", Filters.eq("_id", postId))))
                            .projection(Projections.elemMatch("posts", Filters.eq("_id", postId)))
                            .sort(Sorts.ascending("posts"))
                            .first();

            if(reporter != null && reporter.getPosts() != null && !reporter.getPosts().isEmpty())
                return reporter.getPosts().get(0);
            else
                throw new SocialNewsDataAccessException("Post not in the system");
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Insertion failed: " + me.getMessage());
        }
    }

    public List<Post> postsByReporterIdPrev(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            List<Bson> stages = new ArrayList<>();
            stages.add(Aggregates.match(Filters.and(
                    Filters.eq("reporterId", reporterId),
                    Filters.exists("posts", true))));

            if(offset != null){
                Bson filter = Document.parse(String.format(
                        "{$filter: {" +
                                "input: '$posts'," +
                                "as: 'posts'," +
                                "cond: {" +
                                "    $or: [" +
                                "        {$and: [{$eq:['$$posts.timestamp', new Date(%d)]}, {$gt:['$$posts._id', '%s']}]}," +
                                "        {$gt: ['$$posts.timestamp', new Date(%d)]}]}}}",
                        offset.getTimestamp().getTime(), offset.getId(), offset.getTimestamp().getTime()));

                stages.add(Aggregates.project(Projections.fields(
                        Projections.include("reporterId"),
                        Projections.computed("posts", filter))));
            }
            stages.add(Aggregates.group("$reporterId",Accumulators.push("posts", "$posts")));
            stages.add(Aggregates.project(
                    Projections.fields(
                            Projections.excludeId(),
                            Projections.computed("posts",
                                    Document.parse(String.format(
                                            "{$sortArray:{input:{$filter: {" +
                                                    "input:{" +
                                                    "    $sortArray:{" +
                                                    "        input:{$reduce: {input:'$posts', initialValue: []," +
                                                    "                            in: {$concatArrays: ['$$value', '$$this']}}}," +
                                                    "        sortBy:{'timestamp':1}}}," +
                                                    "cond:{}, limit:%d}}, sortBy:{'timestamp':-1}}}", pageSize))))));

            Reporter reporter = getCollection().aggregate(stages).first();

            return reporter == null ? new ArrayList<>():reporter.getPosts();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public List<Post> postsByReporterIdNext(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            List<Bson> stages = new ArrayList<>();
            stages.add(Aggregates.match(Filters.and(
                    Filters.eq("reporterId", reporterId),
                    Filters.exists("posts", true))));

            if(offset != null){
                Bson filter = Document.parse(String.format(
                        "{$filter: {" +
                                "input: '$posts'," +
                                "as: 'posts'," +
                                "cond: {" +
                                "    $or: [" +
                                "        {$and: [{$eq:['$$posts.timestamp', new Date(%d)]}, {$lt:['$$posts._id', '%s']}]}," +
                                "        {$lt: ['$$posts.timestamp', new Date(%d)]}]}}}",
                        offset.getTimestamp().getTime(), offset.getId(), offset.getTimestamp().getTime()));

                stages.add(Aggregates.project(Projections.fields(
                        Projections.include("reporterId"),
                        Projections.computed("posts", filter))));
            }
            stages.add(Aggregates.group("$reporterId",Accumulators.push("posts", "$posts")));
            stages.add(Aggregates.project(
                    Projections.fields(
                            Projections.include("reporterId"),
                            Projections.computed("posts",
                                    Document.parse(String.format(
                                            "{$filter: {" +
                                                    "input:{" +
                                                    "    $sortArray:{" +
                                                    "        input:{$reduce: {input:'$posts', initialValue: []," +
                                                    "                            in: {$concatArrays: ['$$value', '$$this']}}}," +
                                                    "        sortBy:{'timestamp':-1}}}," +
                                                    "cond:{}, limit:%d}}}", pageSize))))));


            Reporter reporter = getCollection().aggregate(stages).first();

            return reporter == null ? new ArrayList<>():reporter.getPosts();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public ArrayNode postsByHashtagPrev(String hashtag, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            List<Bson> stages = new ArrayList<>();
            String paginationFilter = "";

            if(offset != null){
                paginationFilter = String.format(
                        "{$or: [" +
                                "{$and: [{" +
                                "$eq:['$$posts.timestamp', new Date(%d)]}," +
                                "{$gt:['$$posts._id', '%s']}]}," +
                                "{$gt: ['$$posts.timestamp', new Date(%d)]}]}",
                        offset.getTimestamp().getTime(),
                        offset.getId(),
                        offset.getTimestamp().getTime());
            }
            Bson filter = Document.parse(
                    String.format(
                            "{$filter: {input: '$posts', as: 'posts', cond: {" +
                                    "    $and: [" +
                                    "        {$in: ['%s', {$ifNull: ['$$posts.hashtags', []]}]}" + paginationFilter + "]}}}",
                            hashtag));
            stages.add(Aggregates.match(Filters.eq("posts.hashtags", hashtag)));
            stages.add(Aggregates.project(Projections.fields(
                    Projections.include("reporterId"),
                    Projections.computed("posts", filter))));
            stages.add(Aggregates.addFields(new Field<>("posts.reporterId", "$reporterId")));
            stages.add(Aggregates.group("",Accumulators.push("posts", "$posts")));
            stages.add(Aggregates.project(
                    Projections.fields(
                            Projections.excludeId(),
                            Projections.computed("posts",
                                    Document.parse(String.format(
                                            "{$sortArray:{input:{$filter: {" +
                                                    "input:{" +
                                                    "    $sortArray:{" +
                                                    "        input:{$reduce: {input:'$posts', initialValue: []," +
                                                    "                            in: {$concatArrays: ['$$value', '$$this']}}}," +
                                                    "        sortBy:{'timestamp':1}}}," +
                                                    "cond:{}, limit:%d}}, sortBy:{'timestamp':-1}}}", pageSize))))));

            List<Document> posts = new ArrayList<>();
            getRawCollection("reporters").aggregate(stages).into(posts);
            return (ArrayNode) new ObjectMapper().valueToTree(posts).get(0).get("posts");
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public ArrayNode postsByHashtagNext(String hashtag, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            List<Bson> stages = new ArrayList<>();
            String paginationFilter = "";

            if(offset != null){
                paginationFilter = String.format(
                        "{$or: [" +
                                "{$and: [{" +
                                "$eq:['$$posts.timestamp', new Date(%d)]}," +
                                "{$lt:['$$posts._id', '%s']}]}," +
                                "{$lt: ['$$posts.timestamp', new Date(%d)]}]}",
                        offset.getTimestamp().getTime(),
                        offset.getId(),
                        offset.getTimestamp().getTime());
            }
            Bson filter = Document.parse(
                    String.format(
                            "{$filter: {input: '$posts', as: 'posts', cond: {" +
                                    "    $and: [" +
                                    "        {$in: ['%s', {$ifNull: ['$$posts.hashtags', []]}]}" + paginationFilter + "]}}}",
                            hashtag));
            stages.add(Aggregates.match(Filters.eq("posts.hashtags", hashtag)));
            stages.add(Aggregates.project(Projections.fields(
                    Projections.include("reporterId"),
                    Projections.computed("posts", filter))));
            stages.add(Aggregates.addFields(new Field<>("posts.reporterId", "$reporterId")));
            stages.add(Aggregates.group("",Accumulators.push("posts", "$posts")));
            stages.add(Aggregates.project(
                    Projections.fields(
                            Projections.excludeId(),
                            Projections.computed("posts",
                                    Document.parse(String.format(
                                            "{$filter: {" +
                                                    "input:{" +
                                                    "    $sortArray:{" +
                                                    "        input:{$reduce: {input:'$posts', initialValue: []," +
                                                    "                            in: {$concatArrays: ['$$value', '$$this']}}}," +
                                                    "        sortBy:{'timestamp':-1}}}," +
                                                    "cond:{}, limit:%d}}}", pageSize))))));

            List<Document> posts = new ArrayList<>();
            getRawCollection("reporters").aggregate(stages).into(posts);
            if(posts.size() > 0)
                return (ArrayNode) new ObjectMapper().valueToTree(posts).get(0).get("posts");
            else
                return new ObjectMapper().valueToTree(new ArrayList<>());
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

            stages.add(Aggregates.match(Filters.and(
                    Filters.eq("reporterId", reporterId),
                    Filters.exists("posts", true)))
            );
            stages.add(Aggregates.project(Projections.fields(
                    Projections.excludeId(),
                    Projections.include("posts", "reporterId"),
                    Projections.computed("posts",Document.parse(String.format(
                        "{$filter: {" +
                            "input: '$posts'," +
                            "as: 'posts'," +
                                "cond: {$and:[" +
                                "{$gte: ['$$posts.timestamp', new Date(%d)]}, " +
                                "{$gte: ['$$posts.numOfComment', 1]}]}}}", from.getTime()
                    ))))));
            stages.add(Aggregates.group("$reporterId",
                    Accumulators.first("reporterId","$reporterId"),
                    Accumulators.push("posts","$posts")));
            stages.add(Aggregates.project(Projections.fields(
                    Projections.excludeId(),
                    Projections.include("reporterId"),
                    Projections.computed("posts", Document.parse(String.format(
                        "{$filter: {" +
                        "input:{" +
                        "    $sortArray:{" +
                        "        input:{$reduce: {input:'$posts', initialValue: [], in: {$concatArrays: ['$$value', '$$this']}}}," +
                        "        sortBy:{'numOfComment':-1, timestamp:-1, _id:-1}}}," +
                        "cond:{}, limit: %d}}", nTop)))))
            );

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

    public Long updateNumOfComment(ClientSession session, String postId, Integer increment) throws SocialNewsDataAccessException{
        try{
            return getCollection()
                .updateOne(
                        session,
                        Filters.eq("posts._id", postId),
                        Updates.inc("posts.$.numOfComment", increment))
                .getModifiedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Updated failed: " + me.getMessage());
        }
    }
}
