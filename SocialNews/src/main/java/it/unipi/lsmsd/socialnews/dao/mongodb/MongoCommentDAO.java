package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.MongoException;
import com.mongodb.client.model.*;
import com.mongodb.client.model.densify.DensifyRange;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Comment;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.*;
import java.util.stream.IntStream;

public class MongoCommentDAO extends MongoDAO<Comment> {

    public MongoCommentDAO() {
        super("comments", Comment.class);
    }

    public String createComment(Comment newComment) throws SocialNewsDataAccessException {
        try {
            InsertOneResult result = getCollection().insertOne(newComment);
            return Objects.requireNonNull(result.getInsertedId()).asString().getValue();
        }
        catch (NullPointerException | MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Insertion failed: " + me.getMessage());
        }
    }

    public List<Comment> commentsByPostId(String postId, Integer pageSize) throws SocialNewsDataAccessException {
        return commentsByPostId(postId, null, pageSize);
    }

    public List<Comment> commentsByPostId(String postId, Comment offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            List<Comment> comments = new ArrayList<>();
            Bson filter = Filters.eq("post._id", postId);
            if(offset != null)
                filter = Filters.and(
                        filter,
                        Filters.or(
                                Filters.and(
                                        Filters.gte("timestamp", offset.getTimestamp()),
                                        Filters.gt("reader._id", offset.getReader().getId())
                                ),
                                Filters.gt("timestamp", offset.getTimestamp())
                        ));

            getCollection()
                    .find(filter)
                    .sort(Sorts.ascending("timestamp", "reader._id"))
                    .limit(pageSize)
                    .into(comments);
            return comments;
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public Long removeCommentsByPostId(String postId) throws SocialNewsDataAccessException {
        try{
            DeleteResult result = getCollection().deleteMany(Filters.eq("post._id", postId));
            return result.getDeletedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Deletion failed: " + me.getMessage());
        }
    }

    public Long removeCommentsByReaderId(String readerId) throws SocialNewsDataAccessException {
        try{
            DeleteResult result = getCollection().deleteMany(Filters.eq("reader._id", readerId));
            return result.getDeletedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Deletion failed: " + me.getMessage());
        }
    }

    public Long removeComment(String commentId) throws SocialNewsDataAccessException {
        try{
            DeleteResult result = getCollection().deleteOne(Filters.eq("_id", commentId));
            return result.getDeletedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Deletion failed: " + me.getMessage());
        }
    }

    public ArrayNode latestMostActiveReaders(Integer topN, Date from) throws SocialNewsDataAccessException{
        try{
            List<Bson> stages = new ArrayList<>();
            stages.add(Aggregates.match(Filters.gte("timestamp", from)));
            stages.add(Aggregates.group(
                    "$reader._id",
                    Accumulators.first("fullName","$reader.fullName"),
                    Accumulators.sum("numOfComment",1)));
            stages.add(Aggregates.sort(Sorts.descending("numOfComment")));
            stages.add(Aggregates.limit(topN));

            List<Document> docs = new ArrayList<>();
            getRawCollection("comments").aggregate(stages).into(docs);

            return new ObjectMapper().valueToTree(docs);
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public ArrayNode latestHottestMomentsOfDay(Integer windowSize, Date from)
            throws SocialNewsDataAccessException{
        try{
            if(24 % windowSize != 0)
                throw new IllegalArgumentException();
            List<Integer> boundaries = new ArrayList<>();
            IntStream.iterate(0, n -> n + windowSize).limit(24/ windowSize + 1).forEach(boundaries::add);

            List<Bson> stages = new ArrayList<>();
            stages.add(Aggregates.match(Filters.gte("timestamp", from)));
            stages.add(Aggregates.bucket(
                    Document.parse("{$hour:'$timestamp'}"),
                    boundaries,
                    new BucketOptions().output(new BsonField("count", Document.parse("{ $sum: 1 }")))));
            stages.add(Aggregates.densify("_id", DensifyRange.rangeWithStep(0,24, windowSize)));
            stages.add(Aggregates.set(
                    new Field<>("count", Document.parse("{$cond: [{$not: ['$count']}, 0, '$count']}"))));
            stages.add(Aggregates.project(Projections.fields(
                    Projections.exclude("_id"),
                    Projections.include("count"),
                    Projections.computed("lowerBound", Document.parse("{$mod:['$_id',24]}")),
                    Projections.computed("upperBound", Document.parse(String.format("{$mod:[{$add:['$_id',%d]},24]}",
                            windowSize))
            ))));

            List<Document> docs = new ArrayList<>();
            getRawCollection("comments").aggregate(stages).into(docs);

            return new ObjectMapper().valueToTree(docs);
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
        catch (IllegalArgumentException ex){
            throw new SocialNewsDataAccessException("Query failed: windowSize not divisor of 24");
        }
    }
}
