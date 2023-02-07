package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoException;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import org.bson.Document;
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

    public Long removeReader(String readerId) throws SocialNewsDataAccessException {
        try{
            DeleteResult result = getCollection().deleteOne(Filters.eq("_id", readerId));
            return result.getDeletedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Deletion failed: " + me.getMessage());
        }
    }

    public ObjectNode genderStatistic() throws SocialNewsDataAccessException{
        try{
            List<Bson> stages = new ArrayList<>();
            stages.add(Aggregates.match(Filters.exists("isAdmin", false)));
            stages.add(Aggregates.bucket(Document.parse(
                            "        {$switch: { branches: [" +
                                    "            {case: {$eq:[{$toLower: '$gender'}, 'male']}, then: 0 }," +
                                    "            {case: {$eq:[{$toLower: '$gender'}, 'female']}, then: 1 }]," +
                                    "            default: -1}}}"),
                    List.of(0,1,2),
                    new BucketOptions()
                            .defaultBucket("-1")
                            .output(List.of(
                                    new BsonField("count", Document.parse("{ $sum: 1 }")),
                                    new BsonField("gender", Document.parse("{$first: {$cond: {if:{$in:['$gender',['male','female']]}, then:'$gender', else:'other'}}}"))))));
            stages.add(Aggregates.project(Projections.exclude("_id")));

            List<Document> docs = new ArrayList<>();
            getRawCollection("users").aggregate(stages).into(docs);
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode obj = mapper.createObjectNode();
            for(Document doc : docs){
                obj.put(doc.getString("gender"), doc.getInteger("count"));
            }
            return obj;
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public ArrayNode nationalityStatistic() throws SocialNewsDataAccessException{
        try{
            List<Bson> stages = new ArrayList<>();
            stages.add(Aggregates.match(Filters.exists("isAdmin", false)));
            stages.add(Aggregates.group(Document.parse("{$toLower: '$country'}"),
                    Accumulators.first("country","$country"),
                    Accumulators.sum("count",1)));
            stages.add(Aggregates.project(Projections.exclude("_id")));
            stages.add(Aggregates.sort(Sorts.descending("count")));

            List<Document> docs = new ArrayList<>();
            getRawCollection("users").aggregate(stages).into(docs);
            return new ObjectMapper().valueToTree(docs);
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }
}
