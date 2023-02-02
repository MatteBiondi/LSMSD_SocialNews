package it.unipi.lsmsd.socialnews.dao.neo4j;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.neo4j.Reader;
import it.unipi.lsmsd.socialnews.dao.model.neo4j.Reporter;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReaderNeo4jDAO{
    private final Neo4jConnection neo4jConnection;

    public ReaderNeo4jDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addReader(Reader reader) throws SocialNewsDataAccessException {
        String query ="CREATE (r:Reader {reader_id: $readerId})";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", reader.getReaderId());

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Reader.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Reader creation failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }

    public void followReporter(String readerId, String reporterId) throws SocialNewsDataAccessException {
        String query ="MATCH (rd:Reader {reader_id: $readerId}) " +
                "MATCH (rp:Reporter {reporter_id: $reporterId}) " +
                "CREATE (rd) -[:FOLLOW]-> (rp)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("reporterId", reporterId);

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Reader.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Following action failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }

    // READ OPERATIONS

    public Reader getReaderById(String readerId) throws SocialNewsDataAccessException {
        Reader r = null;

        try {
            r = neo4jConnection.getNeo4jSession().load(Reader.class, readerId);
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reader reading failed: "+ e.getMessage());
        }

        return r;
    }

    public List<Reporter> getFollowingByReaderId(String readerId, int limit, int offset) throws SocialNewsDataAccessException {

        String query = "MATCH (:Reader {reader_id: $readerId})-[:FOLLOW]->(following:Reporter) " +
                "RETURN following " +
                "ORDER BY following.reporter_id " +
                "SKIP $offset " +
                "LIMIT $limit";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("offset", offset);
        parameters.put("limit", limit);

        List<Reporter> result = null;
        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            result = (List<Reporter>) session.query(Reporter.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Following reading failed: "+ e.getMessage());
        } finally {
            tx.close();
        }

        return result;
    }


    // DELETE OPERATIONS

    public void deleteReader(String readerId) throws SocialNewsDataAccessException {
        String query = "MATCH (r:Reader {reader_id: $readerId}) "+
                "OPTIONAL MATCH (r) -[rep:REPORT]-> () "+
                "OPTIONAL MATCH (r) -[f:FOLLOW]-> () "+
                "DELETE rep " +
                "DELETE f " +
                "DELETE r";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Reader.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Reader deletion failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }

    public void unfollowReporter(String readerId, String reporterId) throws SocialNewsDataAccessException {
        String query = "MATCH (rd:Reader {reader_id: $readerId}) -[f:FOLLOW]-> (rp:Reporter {reporter_id: $reporterId}) "+
                "DELETE f";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("reporterId", reporterId);

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Reader.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Unfollowing action failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }


    //STATISTICS OPERATIONS

    public List<Reporter> suggestReporters(String readerId, int limitListLen) throws SocialNewsDataAccessException {

        String query = "MATCH (r:Reporter) " +
                "OPTIONAL MATCH (r)  <-[f:FOLLOW]- (rr:Reader) "+
                "WHERE rr.reader_id <> $readerId " +
                "RETURN r, count(f) as NumFollower " +
                "ORDER BY NumFollower DESC " +
                "LIMIT $limit";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("readerId", readerId);
        parameters.put("limit", limitListLen);

        List<Reporter> result = null;
        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            result = (List<Reporter>) session.query(Reporter.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Reporters suggestion failed: "+ e.getMessage());
        } finally {
            tx.close();
        }

        return  result;
    }
}
