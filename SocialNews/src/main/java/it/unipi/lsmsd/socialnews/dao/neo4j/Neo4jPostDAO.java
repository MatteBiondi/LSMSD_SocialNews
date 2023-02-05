package it.unipi.lsmsd.socialnews.dao.neo4j;


import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import org.neo4j.driver.Query;
import org.neo4j.driver.Session;


import static org.neo4j.driver.Values.parameters;

public class Neo4jPostDAO {
    private final Neo4jConnection neo4jConnection;

    public Neo4jPostDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public Integer addPost(String reporterId, Post post) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query( "MATCH (r:Reporter {reporterId: $reporterId}) "+
                            "MERGE (p:Post {postId: $postId}) " +
                            "CREATE (r) -[:WRITE]-> (p)",
                    parameters("reporterId", reporterId, "postId", post.getId()));

            return session.writeTransaction(tx -> tx.run(query)).consume().counters().nodesCreated();
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Post creation failed: "+ e.getMessage());
        }
    }


    // DELETE OPERATIONS

    public Integer deletePost(String postId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (p:Post {postId: $postId}) <-[rel]-() " +
                            "DELETE rel "+
                            "DELETE p",
                    parameters("postId", postId));

            return session.writeTransaction(tx -> tx.run(query)).consume().counters().nodesDeleted();
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Post deletion failed: "+ e.getMessage());
        }
    }

    public Integer deletePostsByReporterId(String reporterId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (p:Post) <-[w:WRITE]-(:Reporter {reporterId: $reporterId}) "+
                            "OPTIONAL MATCH (p) <-[rep:REPORT]- () "+
                            "DELETE rep " +
                            "DELETE w " +
                            "DELETE p",
                    parameters("reporterId", reporterId));

            return session.writeTransaction(tx -> tx.run(query)).consume().counters().nodesDeleted();
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Posts deletion failed: "+ e.getMessage());
        }
    }
}
