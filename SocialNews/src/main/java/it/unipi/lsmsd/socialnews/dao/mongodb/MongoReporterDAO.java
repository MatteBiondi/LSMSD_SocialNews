package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.*;

public class MongoReporterDAO extends MongoDAO<Reporter> {

    public MongoReporterDAO() {
        super("reporters", Reporter.class);
    }

    public String register(ClientSession session, Reporter newReporter) throws SocialNewsDataAccessException {
        try {
            InsertOneResult result = getCollection().insertOne(session, newReporter);
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

    public List<Reporter> reportersByFullNamePrev(String fullNamePattern, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        List<Reporter> reporters = new ArrayList<>();
        List<Bson> stages = new ArrayList<>();
        StringBuilder regex = new StringBuilder();

        String[] subPatterns = fullNamePattern.trim().split(" ");
        Arrays.stream(subPatterns).forEach(subPattern -> regex.append(String.format("(?=.*\\b%s.*\\b)", subPattern)));
        regex.append(".*");
        Bson filter = Filters.and(
                Filters.exists("email", true),
                Filters.regex("fullName", regex.toString(),"i")
        );

        if(offset != null){
            filter = Filters.and(
                    filter,
                    Filters.or(
                            Filters.and(
                                    Filters.lte("fullName", offset.getFullName()),
                                    Filters.lt("reporterId", offset.getReporterId())
                            ),
                            Filters.lt("fullName", offset.getFullName())
                    ));
        }
        stages.add(Aggregates.match(filter));
        stages.add(Aggregates.project(Projections.include("fullName","reporterId", "picture")));
        stages.add(Aggregates.sort(Sorts.descending("fullName", "reporterId")));
        stages.add(Aggregates.limit(pageSize));
        stages.add(Aggregates.sort(Sorts.ascending("fullName", "reporterId")));

        try{
            getCollection().aggregate(stages).into(reporters);
            return reporters;
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public List<Reporter> reportersByFullNameNext(String fullNamePattern, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        ArrayList<Reporter> reporters = new ArrayList<>();

        StringBuilder regex = new StringBuilder();
        String[] subPatterns = fullNamePattern.trim().split(" ");
        Arrays.stream(subPatterns).forEach(subPattern -> regex.append(String.format("(?=.*\\b%s.*\\b)", subPattern)));
        regex.append(".*");
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
                    .projection(Projections.include("fullName","reporterId", "picture"))
                    .sort(Sorts.ascending("fullName", "reporterId"))
                    .into(reporters);

            return reporters;
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public List<Reporter> allReportersPrev(Reporter filter, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            ArrayList<Reporter> reporters = new ArrayList<>();
            Bson filterDoc = Filters.exists("email", true);
            if(offset != null)
                filterDoc = Filters.and(
                        filterDoc,
                        Filters.or(
                                Filters.and(
                                        Filters.lte("fullName", offset.getFullName()),
                                        Filters.lt("reporterId", offset.getReporterId())
                                ),
                                Filters.lt("fullName", offset.getFullName())
                        ));

            if(filter != null && filter.getEmail() != null && !filter.getEmail().isEmpty()) {
                filterDoc = Filters.and(filterDoc, Filters.regex("email", filter.getEmail()));
            }

            List<Bson> stages = new ArrayList<>();

            stages.add(Aggregates.match(filterDoc));
            stages.add(Aggregates.project(Projections.exclude("posts", "password")));
            stages.add(Aggregates.sort(Sorts.descending("fullName", "_id")));
            stages.add(Aggregates.limit(pageSize));
            stages.add(Aggregates.sort(Sorts.ascending("fullName", "_id")));

            getCollection().aggregate(stages).into(reporters);

            return reporters;
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }

    public List<Reporter> allReportersNext(Reporter filter, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        try{
            ArrayList<Reporter> reporters = new ArrayList<>();
            Bson filterDoc = Filters.exists("email", true);
            if(offset != null)
                filterDoc = Filters.and(
                        filterDoc,
                        Filters.or(
                                Filters.and(
                                        Filters.gte("fullName", offset.getFullName()),
                                        Filters.gt("reporterId", offset.getReporterId())
                                ),
                                Filters.gt("fullName", offset.getFullName())
                        ));

            if(filter != null && filter.getEmail() != null && !filter.getEmail().isEmpty()) {
                filterDoc = Filters.and(filterDoc, Filters.regex("email", filter.getEmail()));
            }

            getCollection()
                .find(filterDoc)
                .projection(Projections.exclude("posts", "password"))
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

    public Long removeReporter(ClientSession session, String reporterId) throws SocialNewsDataAccessException {
        try{
            // Remove reporter and posts
            DeleteResult result = getCollection()
                    .deleteMany(session, Filters.eq("reporterId", reporterId));

            // Remove associated comments
            getRawCollection("comments")
                    .deleteMany(session, Filters.eq("post.reporterId", reporterId));

            return result.getDeletedCount();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Deletion failed: " + me.getMessage());
        }
    }

    public Boolean checkAndSwapDocument(String reporterEmail) throws SocialNewsDataAccessException{
        try (ClientSession session = openSession()){
            return session.withTransaction(() -> {
                try {
                    List<Bson> stages = new ArrayList<>();
                    stages.add(Aggregates.match(Filters.eq("email", reporterEmail)));
                    stages.add(Aggregates.project(Projections.computed(
                            "sizeMB",
                            Document.parse(String.format("{$divide:[{$bsonSize:'$$ROOT'}, %d]}}", 1024*1024))))
                    );
                    Document docSize = getRawCollection("reporters").aggregate(session, stages).first();

                    if (docSize.getDouble("sizeMB") > MAX_DOC_SIZE_MB) {
                        Reporter reporter = reporterByEmail(reporterEmail);
                        reporter.setId(UUID.randomUUID().toString());
                        getCollection().updateOne(session, Filters.eq("email", reporterEmail), Updates.combine(
                                Updates.unset("email"),
                                Updates.unset("password"),
                                Updates.unset("gender"),
                                Updates.unset("location"),
                                Updates.unset("dateOfBirth"),
                                Updates.unset("cell"),
                                Updates.unset("picture"),
                                Updates.unset("numOfReport")

                        ));
                        register(session, reporter);
                        return true;
                    }
                    return false;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw new SocialNewsDataAccessException("Operation failed: " + ex.getMessage());
        }
    }
}
