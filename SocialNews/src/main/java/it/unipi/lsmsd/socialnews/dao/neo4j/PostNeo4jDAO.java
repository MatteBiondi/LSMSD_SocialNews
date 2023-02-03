package it.unipi.lsmsd.socialnews.dao.neo4j;


import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import org.neo4j.driver.Query;
import org.neo4j.driver.Session;


import static org.neo4j.driver.Values.parameters;

public class PostNeo4jDAO{
    private final Neo4jConnection neo4jConnection;

    public PostNeo4jDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addPost(String reporterId, Post post) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query( "MATCH (r:Reporter {reporter_id: $reporterId}) "+
                            "MERGE (p:Post {post_id: $postId}) " +
                            "CREATE (r) -[:WRITE]-> (p)",
                    parameters("reporterId", reporterId, "postId", post.getId()));

            session.writeTransaction(tx -> {
               tx.run(query);
               return 1;
            });
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Post creation failed: "+ e.getMessage());
        }
    }


    // READ OPERATIONS


    // DELETE OPERATIONS

    public void deletePost(String postId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (p:Post {post_id: $postId}) <-[rel]-() " +
                            "DELETE rel "+
                            "DELETE p",
                    parameters("postId", postId));

            session.writeTransaction(tx -> {
                tx.run(query);
                return 1;
            });
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Post deletion failed: "+ e.getMessage());
        }
    }

    public void deletePostsByReporterId(String reporterId) throws SocialNewsDataAccessException {
        try(Session session = neo4jConnection.getNeo4jSession()){
            Query query = new Query(
                    "MATCH (p:Post) <-[w:WRITE]-(:Reporter {reporter_id: $reporterId}) "+
                            "OPTIONAL MATCH (p) <-[rep:REPORT]- () "+
                            "DELETE rep " +
                            "DELETE w " +
                            "DELETE p",
                    parameters("reporterId", reporterId));

            session.writeTransaction(tx -> {
                tx.run(query);
                return 1;
            });
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Posts deletion failed: "+ e.getMessage());
        }
    }
}
