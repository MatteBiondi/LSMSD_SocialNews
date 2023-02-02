package it.unipi.lsmsd.socialnews.dao.neo4j;


import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.neo4j.Post;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;

import java.util.HashMap;
import java.util.Map;

public class PostNeo4jDAO{
    private final Neo4jConnection neo4jConnection;

    public PostNeo4jDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addPost(Post post) throws SocialNewsDataAccessException {
        String query =
                "MATCH (r:Reporter {reporter_id: $reporterId}) "+
                        "MERGE (p:Post {post_id: $postId}) " +
                        "CREATE (r) -[:WRITE]-> (p)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", post.getReporter().getReporterId());
        parameters.put("postId", post.getPostId());

        Session session = neo4jConnection.getNeo4jSession();

        Transaction tx = session.beginTransaction();
        try {
            session.query(Post.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Post creation failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }


    // READ OPERATIONS

    public Post getPostById(String postId) throws SocialNewsDataAccessException {
        Post p = null;

        try {
            p = neo4jConnection.getNeo4jSession().load(Post.class, postId);
        } catch (Exception e){
            e.printStackTrace();
            throw new SocialNewsDataAccessException("Post reading failed: "+ e.getMessage());
        }

        return p;
    }

    // DELETE OPERATIONS

    public void deletePost(String postId) throws SocialNewsDataAccessException {
        String query = "MATCH (p:Post {post_id: $postId}) <-[rel]-() " +
                "DELETE rel "+
                "DELETE p";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("postId", postId);

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Post.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Post deletion failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }

    public void deletePostsByReporterId(String reporterId) throws SocialNewsDataAccessException {
        String query = "MATCH (p:Post) <-[w:WRITE]-(r:Reporter {reporter_id: $reporterId}) "+
                "OPTIONAL MATCH (p) <-[rep:REPORT]- () "+
                "DELETE rep " +
                "DELETE w " +
                "DELETE p";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", reporterId);

        Session session = neo4jConnection.getNeo4jSession();
        Transaction tx = session.beginTransaction();
        try {
            session.query(Post.class, query, parameters);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
            throw new SocialNewsDataAccessException("Posts deletion failed: "+ e.getMessage());
        } finally {
            tx.close();
        }
    }
}
