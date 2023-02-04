package it.unipi.lsmsd.socialnews.dao.neo4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import org.neo4j.driver.*;

import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class Neo4jReaderDAO {
    private final Neo4jConnection neo4jConnection;

    public Neo4jReaderDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addReader(Reader reader) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query( "CREATE (:Reader {readerId: $readerId})",
                    parameters("readerId", reader.getId()));

            session.writeTransaction(tx -> tx.run(query));
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reader creation failed: "+ e.getMessage());
        }
    }

    public void followReporter(String readerId, String reporterId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (rd:Reader {readerId: $readerId}) " +
                            "MATCH (rp:Reporter {reporterId: $reporterId}) " +
                            "CREATE (rd) -[:FOLLOW]-> (rp)",
                    parameters("readerId", readerId, "reporterId", reporterId));

            session.writeTransaction(tx -> tx.run(query));
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Following action failed: "+ e.getMessage());
        }
    }

    // READ OPERATIONS

    public List<Reporter> getFollowingByReaderId(String readerId, int limit, int offset) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (:Reader {readerId: $readerId})-[:FOLLOW]->(following:Reporter) " +
                            "RETURN following " +
                            "ORDER BY following.reporterId " +
                            "SKIP $offset " +
                            "LIMIT $limit",
                    parameters("readerId", readerId, "offset", offset, "limit", limit));
            return session.readTransaction(tx ->
                tx.run(query).list( record ->
                        new ObjectMapper().convertValue(record.get("following").asMap(), Reporter.class))
            );
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Following reading failed: "+ e.getMessage());
        }
    }


    // DELETE OPERATIONS

    public void deleteReader(String readerId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (r:Reader {readerId: $readerId}) "+
                            "OPTIONAL MATCH (r) -[rep:REPORT]-> () "+
                            "OPTIONAL MATCH (r) -[f:FOLLOW]-> () "+
                            "DELETE rep " +
                            "DELETE f " +
                            "DELETE r",
                    parameters("readerId", readerId));

            session.writeTransaction(tx -> tx.run(query));
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reader deletion failed: "+ e.getMessage());
        }
    }

    public void unfollowReporter(String readerId, String reporterId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (:Reader {readerId: $readerId}) -[f:FOLLOW]-> (:Reporter {reporterId: $reporterId}) "+
                            "DELETE f",
                    parameters("readerId", readerId, "reporterId", reporterId));

            session.writeTransaction(tx -> tx.run(query));
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Unfollowing action failed: "+ e.getMessage());
        }
    }


    //STATISTICS OPERATIONS

    public List<Reporter> suggestReporters(String readerId, int limitListLen) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (r:Reporter) " +
                            "OPTIONAL MATCH (r)  <-[f:FOLLOW]- (rr:Reader) "+
                            "WHERE rr.readerId <> $readerId " +
                            "WITH r as suggestedReporters, count(f) as NumFollower "+
                            "RETURN suggestedReporters " +
                            "ORDER BY NumFollower DESC " +
                            "LIMIT $limit",
                    parameters("readerId", readerId, "limit", limitListLen));

            return session.readTransaction(tx ->
                tx.run(query).list( record ->
                        new ObjectMapper().convertValue(record.get("suggestedReporters").asMap(), Reporter.class))
            );
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reporters suggestion failed: "+ e.getMessage());
        }
    }
}
