package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reader;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MongoReaderDAO extends MongoDAO<Reader> {

    public MongoReaderDAO() {
        super("users", Reader.class);
    }

    public String register(Reader newReader) throws SocialNewsDataAccessException {
        try {
            InsertOneResult result = getCollection().insertOne(newReader);
            return Objects.requireNonNull(result.getInsertedId()).asString().getValue();
        }
        catch (NullPointerException | MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Insertion failed: " + me.getMessage());
        }
    }

    public Reader authenticate(String email, String password) throws SocialNewsDataAccessException {
        try{
            return getCollection()
                    .find(Filters.and(
                            Filters.eq("email", email),
                            Filters.eq("password", password),
                            Filters.exists("isAdmin", false)))
                    .first();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public Reader readerByEmail(String email) throws SocialNewsDataAccessException {
        try{
            return getCollection()
                    .find(Filters.and(
                            Filters.eq("email", email),
                            Filters.exists("isAdmin", false)))
                    .first();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }

    }

    public List<Reader> allReaders(Integer pageSize) throws SocialNewsDataAccessException {
        return allReaders(null, pageSize);
    }

    public List<Reader> allReaders(Reader offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            ArrayList<Reader> readers = new ArrayList<>();
            Bson filter = Filters.exists("isAdmin", false);

            if(offset != null)
                filter = Filters.and(
                        filter,
                        Filters.or(
                                Filters.and(
                                        Filters.gte("fullName", offset.getFullName()),
                                        Filters.gt("_id", offset.getId())
                                ),
                                Filters.gt("fullName", offset.getFullName())
                        ));

            getCollection()
                    .find(filter)
                    .sort(Sorts.ascending("fullName", "_id"))
                    .limit(pageSize)
                    .into(readers);

            return readers;
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public Long removeReader(String email) throws SocialNewsDataAccessException {
        try{
            DeleteResult result = getCollection().deleteOne(Filters.eq("email", email));
            return result.getDeletedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Deletion failed: " + me.getMessage());
        }
    }
}
