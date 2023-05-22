package it.unipi.lsmsd.socialnews.dao.neo4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Report;
import org.neo4j.driver.Query;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;

import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class Neo4jReportDAO {

    private final Neo4jConnection neo4jConnection;

    public Neo4jReportDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public Integer addReport(Report report, String reporterId) throws SocialNewsDataAccessException {
        String postId = report.getPostId();
        String readerId = report.getReaderId();

        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (reporter:Reporter {reporterId: $reporterId}) " +
                            "MERGE (post:Post {postId: $postId}) <-[:WRITE]- (reporter) "+
                            "MERGE (reader:Reader {readerId: $readerId}) " +
                            "CREATE (reader) -[report:REPORT {reportId:$reportId, timestamp: $timestamp, text: $text}]-> (post) ",
                    parameters("readerId", readerId,
                            "reporterId", reporterId,
                            "postId", postId,
                            "reportId", report.getReportId(),
                            "timestamp", report.getTimestamp().getTime(),
                            "text", report.getText())
            );

            return session.writeTransaction(tx -> tx.run(query)).consume().counters().relationshipsCreated();
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Report creation by object failed: "+ e.getMessage());
        }
    }


    // READ OPERATIONS

    public Report getReportById(String reportId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (rr:Reader) -[r:REPORT]-> (p: Post) " +
                            "WHERE r.reportId = $reportId " +
                            "RETURN rr.readerId as readerId,r as report,p.postId as postId",
                    parameters("reportId", reportId));
            return session.readTransaction(tx -> {
                        Result result = tx.run(query);
                        Record record = result.single();
                        Report r = new ObjectMapper().convertValue(
                                record.get("report").asMap(), Report.class);
                        r.setReportId(reportId);
                        r.setPostId(record.get("postId").asString());
                        r.setReaderId(record.get("readerId").asString());
                        return r;
                    }
            );
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Report reading failed: "+ e.getMessage());
        }
    }

    public List<Report> getReportsByReporterId(String reporterId, int limit, int offset) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (reporter:Reporter) -[:WRITE]-> (post:Post) <-[report:REPORT]- (reader:Reader) " +
                            "WHERE reporter.reporterId = $reporterId " +
                            "RETURN reader.readerId as readerId, report, post.postId as postId "+
                            "ORDER BY report.timestamp DESC " +
                            "SKIP $offset " +
                            "LIMIT $limit",
                    parameters("reporterId", reporterId, "offset", offset, "limit", limit));

            return session.readTransaction(tx ->
                    tx.run(query).list( record -> {
                        Report rep = new ObjectMapper().convertValue(record.get("report").asMap(), Report.class);
                        rep.setPostId(record.get("postId").asString());
                        rep.setReaderId(record.get("readerId").asString());
                        return rep;
                    })
            );
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reports reading failed: "+ e.getMessage());
        }
    }


    // DELETE OPERATIONS

    public ObjectNode deleteReport(String reportId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (reader:Reader) -[report:REPORT]-> (post:Post) "+
                            "MATCH (reporter:Reporter) -[:WRITE]-> (post) "+
                            "WHERE report.reportId = $reportId "+
                            "DELETE report "+
                            "RETURN reporter.reporterId as reporterId",
                    parameters("reportId", reportId)
            );
            return session.writeTransaction(tx -> {
                Result result = tx.run(query);
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode toReturn = objectMapper.createObjectNode();
                toReturn.put("reporterId", result.single().get("reporterId").asString());
                toReturn.put("deletedCounter", result.consume().counters().relationshipsDeleted());
                return toReturn;
            });
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Report deletion failed: "+ e.getMessage());
        }
    }
}
