package it.unipi.lsmsd.socialnews.dao.neo4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import org.neo4j.driver.Query;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class Neo4jReporterDAO {
    private final Neo4jConnection neo4jConnection;

    public Neo4jReporterDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public Integer addReporter(Reporter reporter) throws SocialNewsDataAccessException{
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "CREATE (:Reporter {reporterId: $reporterId, fullName: $name, picture: $picture})",
                    parameters("reporterId", reporter.getReporterId(),
                            "name", reporter.getFullName(),
                            "picture", reporter.getPicture())
            );

            return session.writeTransaction(tx -> tx.run(query)).consume().counters().nodesCreated();
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reporter creation failed: "+ e.getMessage());
        }
    }


    // READ OPERATIONS

    public Reporter getReporterById (String reporterId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (reporter:Reporter {reporterId: $reporterId}) RETURN reporter",
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

    public Integer getNumOfFollowers(String reporterId) throws SocialNewsDataAccessException{
        return getNumOfFollowers(reporterId, null).get("numFollowers").asInt();
    }

    public ArrayNode getNumOfFollowers(String reporterId, String readerId) throws SocialNewsDataAccessException{
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (reporter:Reporter {reporterId: $reporterId}) <-[:FOLLOW]- (reader:Reader) " +
                            "OPTIONAL MATCH (:Reader {readerId: $readerId}) -[follow:FOLLOW]-> (reporter) " +
                            "RETURN count(reader) as numFollowers, count(follow) as follow",
                    parameters("reporterId", reporterId,
                            "readerId", readerId));
            Record result = session.readTransaction(tx -> tx.run(query).single());
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            arrayNode.add(result.get("numFollowers").asInt());
            arrayNode.add(result.get("follow").asInt());
            return arrayNode;
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Number of follower reading failed: "+ e.getMessage());
        }
    }



    //DELETE OPERATIONS

    public Integer deleteReporter(String reporterId) throws SocialNewsDataAccessException{
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (r:Reporter {reporterId: $reporterId}) " +
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

            return session.writeTransaction(tx -> tx.run(query)).consume().counters().nodesDeleted();
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Reporter deletion failed: "+ e.getMessage());
        }
    }


    //STATISTICS OPERATIONS

    public ArrayNode getMostPopularReporters(int limitTopRanking) throws SocialNewsDataAccessException{
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (r:Reporter) " +
                            "OPTIONAL MATCH (r) <-[f:FOLLOW]- () "+
                            "WITH r as reporter, count(f) as numFollowers " +
                            "RETURN reporter, numFollowers "+
                            "ORDER BY numFollowers DESC " +
                            "LIMIT $limit",
                    parameters("limit", limitTopRanking));

            List<JsonNode> result = session.readTransaction(tx -> {
                Result queryResult = tx.run(query);
                List<JsonNode> nodes = new ArrayList<>();
                while (queryResult.hasNext()) {
                    Record record = queryResult.next();
                    ObjectNode jsonNode = new ObjectMapper().createObjectNode();
                    jsonNode.put("fullName", record.get("reporter").get("fullName").asString());
                    jsonNode.put("numFollowers", record.get("numFollowers").asInt());
                    nodes.add(jsonNode);
                }
                return nodes;
            });
            ArrayNode toReturn = new ObjectMapper().createArrayNode();
            toReturn.addAll(result);

            return toReturn;
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Most popular reporter statistic failed: "+ e.getMessage());
        }
    }
}
