package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import it.unipi.lsmsd.socialnews.dao.ReaderDAO;
import it.unipi.lsmsd.socialnews.model.Reader;

import java.util.Objects;

public class MongoReaderDAO extends MongoDAO<Reader> implements ReaderDAO {

    public MongoReaderDAO() {
        super("users", Reader.class);
    }

    @Override
    public String register(Reader newReader) {
        try {
            InsertOneResult result = getCollection().insertOne(newReader);
            return Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue().toString();
        }
        catch (MongoException me){
            me.printStackTrace();
        }
        return null;
    }

    public Reader authenticate(String email, String password){
        return getCollection()
                .find(Filters.and(
                        Filters.eq("email", email),
                        Filters.eq("password", password),
                        Filters.exists("isAdmin", false)))
                .first();
    }
}
