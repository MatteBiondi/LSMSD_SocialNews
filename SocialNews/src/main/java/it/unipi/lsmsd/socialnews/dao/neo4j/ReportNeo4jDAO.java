package it.unipi.lsmsd.socialnews.dao.neo4j;

import it.unipi.lsmsd.socialnews.dao.model.neo4j.Report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportNeo4jDAO {

    private final Neo4jConnection neo4jConnection;

    public ReportNeo4jDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addReport(Report report){
        String postId = report.getPost().getPostId();
        String readerId = report.getReader().getReaderId();

        String query = "MATCH (reader:Reader {reader_id: $readerId}) " +
                "MATCH (p:Post {post_id: $postId}) "+
                "CREATE (reader) -[r:REPORT {timestamp: $timestamp, text: $text}]-> (p)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("postId", postId);
        parameters.put("timestamp", report.getTimestamp());
        parameters.put("text", report.getText());

        neo4jConnection.getNeo4jSession().query(Report.class, query, parameters);
    }

    public void addReport2(String timestamp, String text, String readerId, String postId){
        String query = "MATCH (reader:Reader {reader_id: $readerId}) " +
                "MATCH (p:Post {post_id: $postId}) "+
                "CREATE (reader) -[r:REPORT {timestamp: $timestamp, text: $text}]-> (p)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("postId", postId);
        parameters.put("timestamp", timestamp);
        parameters.put("text", text);

        neo4jConnection.getNeo4jSession().query(Report.class, query, parameters);
    }


    // READ OPERATIONS

    public Report getReportById(Long reportId){
        return neo4jConnection.getNeo4jSession().load(Report.class, reportId);
    }

    public List<Report> getReportsByReporterId(String reporterId, int limit, int offset){
        String query = "MATCH (r:Reporter) -[:WRITE]-> (p:Post) <-[rep:REPORT]- () " +
                "WHERE r.reporter_id = $reporterId " +
                "RETURN rep "+
                "ORDER BY rep.id ASC " +
                "SKIP $offset " +
                "LIMIT $limit";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", reporterId);
        parameters.put("offset", offset);
        parameters.put("limit", limit);

        String query2 = "MATCH () -[r:REPORT] -> () RETURN r";
        Map<String, Object> parameters2 = new HashMap<>();
        Report rep = ((List<Report>) neo4jConnection.getNeo4jSession().query(Report.class, query2, parameters2)).get(0);
        System.out.println();

        return (List<Report>) neo4jConnection.getNeo4jSession().query(Report.class, query, parameters);
    }


    // DELETE OPERATIONS

    public void deleteReport(Long reportId){
        String query = "MATCH (reader:Reader) <-[r:REPORT {report_id: $reportId}]-(p:Post) "+
                "DELETE r";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportId", reportId);

        neo4jConnection.getNeo4jSession().query(Report.class, query, parameters);
    }
}