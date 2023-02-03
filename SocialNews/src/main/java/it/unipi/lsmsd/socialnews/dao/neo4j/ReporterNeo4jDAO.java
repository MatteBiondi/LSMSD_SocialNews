package it.unipi.lsmsd.socialnews.dao.neo4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import org.neo4j.driver.Query;
import org.neo4j.driver.Session;

import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class ReporterNeo4jDAO{
    private final Neo4jConnection neo4jConnection;

    public ReporterNeo4jDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addReporter(Reporter reporter) throws SocialNewsDataAccessException{
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "CREATE (:Reporter {reporter_id: $reporterId, name: $name, picture: $picture})",
                    parameters("reporterId", reporter.getReporterId(),
                            "name", reporter.getFullName(),
                            "picture", reporter.getPicture())
            );

            session.writeTransaction(tx -> tx.run(query));
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reporter creation failed: "+ e.getMessage());
        }
    }


    // READ OPERATIONS

    public Reporter getReporterById (String reporterId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (reporter:Reporter {reporter_id: $reporterId}) RETURN reporter",
                    parameters("reporterId", reporterId));
            return session.readTransaction(tx ->
                    new ObjectMapper().convertValue(
                            tx.run(query).single().get("reporter").asMap(), Reporter.class)
            );
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reporter reading failed: "+ e.getMessage());
        }
    }

    public int getNumOfFollowers(String reporterId) throws SocialNewsDataAccessException{
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (:Reporter {reporter_id: $reporterId}) <-[:FOLLOW]- (reader:Reader) " +
                            "RETURN count(reader) as numFollowers",
                    parameters("reporterId", reporterId));
            return session.readTransaction(tx -> tx.run(query).single().get("numFollowers").asInt());
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Number of follower reading failed: "+ e.getMessage());
        }
    }



    //DELETE OPERATIONS

    public void deleteReporter(String reporterId) throws SocialNewsDataAccessException{
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (r:Reporter {reporter_id: $reporterId}) " +
                            "OPTIONAL MATCH (r) -[w:WRITE]-> (p:Post) " +
                            "OPTIONAL MATCH (p) <-[rp]- () "+
                            "OPTIONAL MATCH (r) <-[rr]- () "+
                            "DELETE rp "+
                            "DELETE rr "+
                            "DELETE w " +
                            "DELETE p "+
                            "DELETE r ",
                    parameters("reporterId", reporterId)
            );

            session.writeTransaction(tx -> tx.run(query));
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reporter deletion failed: "+ e.getMessage());
        }
    }


    //STATISTICS OPERATIONS

    public List<Reporter> getMostPopularReporters(int limitTopRanking) throws SocialNewsDataAccessException{
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (r:Reporter) " +
                            "OPTIONAL MATCH (r) <-[f:FOLLOW]- () "+
                            "WITH r as reporter, count(f) as numFollowers " +
                            "RETURN reporter "+
                            "ORDER BY numFollowers DESC " +
                            "LIMIT $limit",
                    parameters("limit", limitTopRanking));

            return session.readTransaction(tx ->
                    tx.run(query).list( record ->
                            new ObjectMapper().convertValue(record.get("following").asMap(), Reporter.class))
            );
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Most popular reporter statistic failed: "+ e.getMessage());
        }
    }
}
