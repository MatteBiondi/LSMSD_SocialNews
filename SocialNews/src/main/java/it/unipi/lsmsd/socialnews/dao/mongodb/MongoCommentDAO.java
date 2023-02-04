package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Comment;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            Bson filter = Filters.eq("postId", postId);
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
            DeleteResult result = getCollection().deleteMany(Filters.eq("postId", postId));
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
}
