package it.unipi.lsmsd.socialnews.dao.neo4j;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.neo4j.Reporter;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporterNeo4jDAO{
    private final Neo4jConnection neo4jConnection;

    public ReporterNeo4jDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addReporter(Reporter reporter) throws SocialNewsDataAccessException{
        String query = "CREATE (r:Reporter {reporter_id: $reporterId, name: $name, picture: $picture})";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", reporter.getReporterId());
        parameters.put("name", reporter.getName());
        parameters.put("picture", reporter.getPicture());

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Reporter.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Reporter creation failed : "+ e.getMessage());
        } finally {
            tx.close();
        }
    }


    // READ OPERATIONS

    public Reporter getReporterById (String reporterId) throws SocialNewsDataAccessException {
        Reporter r = null;
        try{
            r = neo4jConnection.getNeo4jSession().load(Reporter.class, reporterId);
        }catch(Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reporter reading failed : "+ e.getMessage());
        }
        return r;
    }

    public int getNumOfFollowers(String reporterId) throws SocialNewsDataAccessException{
        String query = "MATCH (r:Reporter {reporter_id: $reporterId}) <-[:FOLLOW]- (reader:Reader) " +
                "RETURN count(reader)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", reporterId);

        int result=-1;
        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction(Transaction.Type.READ_ONLY);
        try {
            result = session.queryForObject(Integer.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Number of follower reading failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
        return result;
    }



    //DELETE OPERATIONS

    public void deleteReporter(String reporterId) throws SocialNewsDataAccessException{
        String query = "MATCH (r:Reporter {reporter_id: $reporterId}) " +
                "OPTIONAL MATCH (r) -[w:WRITE]-> (p:Post) " +
                "OPTIONAL MATCH (p) <-[rp]- () "+
                "OPTIONAL MATCH (r) <-[rr]- () "+
                "DELETE rp "+
                "DELETE rr "+
                "DELETE w " +
                "DELETE p "+
                "DELETE r ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", reporterId);

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Reporter.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Reporter deletion failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }


    //STATISTICS OPERATIONS

    public List<Reporter> getMostPopularReporters(int limitTopRanking) throws SocialNewsDataAccessException{
        String query = "MATCH (r:Reporter) " +
                "OPTIONAL MATCH (r) <-[f:FOLLOW]- () "+
                "RETURN r, count(f) as NumFollower " +
                "ORDER BY NumFollower DESC " +
                "LIMIT $limit";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("limit", limitTopRanking);

        List<Reporter> result = new ArrayList<>();
        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction(Transaction.Type.READ_ONLY);
        try {
            result = (List<Reporter>) session.query(Reporter.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Most popular reporter statistic failed: "+ e.getMessage());
        } finally {
            tx.close();
        }

        return result;
    }
}
