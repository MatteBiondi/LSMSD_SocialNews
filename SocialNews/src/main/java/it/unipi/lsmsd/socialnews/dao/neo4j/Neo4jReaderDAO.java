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

    public Integer addReader(Reader reader) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query( "CREATE (:Reader {readerId: $readerId})",
                    parameters("readerId", reader.getId()));

            return session.writeTransaction(tx -> tx.run(query)).consume().counters().nodesCreated();
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reader creation failed: "+ e.getMessage());
        }
    }

    public Integer followReporter(String readerId, String reporterId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (rp:Reporter {reporterId: $reporterId}) " +
                            "MERGE (rd:Reader {readerId: $readerId}) " +
                            "CREATE (rd) -[:FOLLOW]-> (rp)",
                    parameters("readerId", readerId, "reporterId", reporterId));

            return session.writeTransaction(tx -> tx.run(query)).consume().counters().relationshipsCreated();
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

    public Integer deleteReader(String readerId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (r:Reader {readerId: $readerId}) "+
                            "OPTIONAL MATCH (r) -[rep:REPORT]-> () "+
                            "OPTIONAL MATCH (r) -[f:FOLLOW]-> () "+
                            "DELETE rep " +
                            "DELETE f " +
                            "DELETE r",
                    parameters("readerId", readerId));

            return session.writeTransaction(tx -> tx.run(query)).consume().counters().nodesDeleted();
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reader deletion failed: "+ e.getMessage());
        }
    }

    public Integer unfollowReporter(String readerId, String reporterId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (:Reader {readerId: $readerId}) -[f:FOLLOW]-> (:Reporter {reporterId: $reporterId}) "+
                            "DELETE f",
                    parameters("readerId", readerId, "reporterId", reporterId));

            return session.writeTransaction(tx -> tx.run(query)).consume().counters().relationshipsDeleted();
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
                            "WHERE NOT (:Reader {readerId: $readerId}) -[:FOLLOW]-> (r) " +
                            "OPTIONAL MATCH (r) <-[f:FOLLOW]-() " +
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
