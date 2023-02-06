package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import org.bson.conversions.Bson;
import java.util.*;

public class MongoReporterDAO extends MongoDAO<Reporter> {

    public MongoReporterDAO() {
        super("reporters", Reporter.class);
    }

    public String register(Reporter newReporter) throws SocialNewsDataAccessException {
        try {
            InsertOneResult result = getCollection().insertOne(newReporter);
            return Objects.requireNonNull(result.getInsertedId()).asString().getValue();
        }
        catch (NullPointerException | MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Insertion failed: " + me.getMessage());
        }
    }

    public Reporter authenticate(String email, String password) throws SocialNewsDataAccessException {
        try{
            return getCollection()
                    .find(Filters.and(
                            Filters.eq("email", email),
                            Filters.eq("password", password)))
                    .first();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public Reporter reporterByEmail(String email) throws SocialNewsDataAccessException {
        try{
            return getCollection()
                    .find(Filters.eq("email", email))
                    .projection(Projections.exclude("posts"))
                    .first();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public Reporter reporterByReporterId(String reporterId, Integer pageSize) throws SocialNewsDataAccessException {
        return reporterByReporterId(reporterId, null, pageSize);
    }

    public Reporter reporterByReporterId(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            List<Bson> stages = new ArrayList<>();

            stages.add(Aggregates.match(Filters.eq("reporterId", reporterId)));
            stages.add(Aggregates.unwind("$posts", new UnwindOptions().preserveNullAndEmptyArrays(true)));
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
                    Accumulators.first("email","$email"),
                    Accumulators.first("reporterId","$reporterId"),
                    Accumulators.first("fullName","$fullName"),
                    Accumulators.first("gender","$gender"),
                    Accumulators.first("location","$location"),
                    Accumulators.first("date","$dateOfBirth"),
                    Accumulators.first("cell","$cell"),
                    Accumulators.first("picture","$picture"),
                    Accumulators.push("posts", "$posts")));

            return getCollection().aggregate(stages).first();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public List<Reporter> reportersByFullName(String fullNamePattern, Integer pageSize) throws SocialNewsDataAccessException {
        return reportersByFullName(fullNamePattern, null, pageSize);
    }

    public List<Reporter> reportersByFullName(String fullNamePattern, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        ArrayList<Reporter> reporters = new ArrayList<>();

        StringBuilder regex = new StringBuilder();
        String[] subPatterns = fullNamePattern.trim().split(" ");
        for (Iterator<String> iter = Arrays.stream(subPatterns).iterator(); iter.hasNext(); ) {
            regex.append(String.format("(%s.*)\\s%s", iter.next(), iter.hasNext() ? "+":"*"));
        }

        Bson filter = Filters.and(
                Filters.exists("email", true),
                Filters.regex("fullName", regex.toString(),"i")
        );
        if(offset != null){
            filter = Filters.and(
                    filter,
                    Filters.or(
                            Filters.and(
                                    Filters.gte("fullName", offset.getFullName()),
                                    Filters.gt("reporterId", offset.getReporterId())
                            ),
                            Filters.gt("fullName", offset.getFullName())
                    ));
        }

        try{
            getCollection()
                    .find(filter)
                    .limit(pageSize)
                    .projection(Projections.exclude("posts"))
                    .sort(Sorts.ascending("fullName", "reporterId"))
                    .into(reporters);

            return reporters;
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public List<Reporter> allReporters(Integer pageSize) throws SocialNewsDataAccessException {
        return allReporters(null, pageSize);
    }

    public List<Reporter> allReporters(Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            ArrayList<Reporter> reporters = new ArrayList<>();
            Bson filter = Filters.exists("email", true);
            if(offset != null)
                filter = Filters.and(
                        filter,
                        Filters.or(
                                Filters.and(
                                        Filters.gte("fullName", offset.getFullName()),
                                        Filters.gt("reporterId", offset.getReporterId())
                                ),
                                Filters.gt("fullName", offset.getFullName())
                        ));

            getCollection()
                    .find(filter)
                    .projection(Projections.exclude("posts"))
                    .sort(Sorts.ascending("fullName", "reporterId"))
                    .limit(pageSize)
                    .into(reporters);

            return reporters;
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public Long removeReporter(String reporterId) throws SocialNewsDataAccessException {
        try{
            DeleteResult result = getCollection()
                    .deleteMany(Filters.eq("reporterId", reporterId));
            return result.getDeletedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Deletion failed: " + me.getMessage());
        }
    }

}
