package it.unipi.lsmsd.socialnews.dao.neo4j;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.neo4j.Report;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportNeo4jDAO {

    private final Neo4jConnection neo4jConnection;

    public ReportNeo4jDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addReport(Report report) throws SocialNewsDataAccessException {
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

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Report.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Report creation by object failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }

    public void addReportByFields(LocalDateTime timestamp, String text, String readerId, String postId) throws SocialNewsDataAccessException {
        String query = "MATCH (reader:Reader {reader_id: $readerId}) " +
                "MATCH (p:Post {post_id: $postId}) "+
                "CREATE (reader) -[r:REPORT {timestamp: $timestamp, text: $text}]-> (p)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("postId", postId);
        parameters.put("timestamp", timestamp.toString());
        parameters.put("text", text);

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Report.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Report creation by fields failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }


    // READ OPERATIONS

    public Report getReportById(Long reportId) throws SocialNewsDataAccessException {
        Report r = null;

        try {
            r = neo4jConnection.getNeo4jSession().load(Report.class, reportId);
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Report reading failed: "+ e.getMessage());
        }

        return r;
    }

    public List<Report> getReportsByReporterId(String reporterId, int limit, int offset) throws SocialNewsDataAccessException {
        String query = "MATCH (reporter:Reporter) -[:WRITE]-> (post:Post) <-[report:REPORT]- (reader:Reader) " +
                "WHERE reporter.reporter_id = $reporterId " +
                "RETURN reader, report, post "+
                "ORDER BY report.id ASC " +
                "SKIP $offset " +
                "LIMIT $limit";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", reporterId);
        parameters.put("offset", offset);
        parameters.put("limit", limit);

        List<Report> result = null;
        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction(Transaction.Type.READ_ONLY);
        try {
            result = (List<Report>) session.query(Report.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Reports reading failed: "+ e.getMessage());
        } finally {
            tx.close();
        }

        return result;
    }


    // DELETE OPERATIONS

    public void deleteReport(Long reportId) throws SocialNewsDataAccessException {
        String query = "MATCH (:Reader) -[r:REPORT]-> (:Post) "+
                "WHERE id(r) = $reportId "+
                "DELETE r";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportId", reportId);

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Report.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Report deletion failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }
}
