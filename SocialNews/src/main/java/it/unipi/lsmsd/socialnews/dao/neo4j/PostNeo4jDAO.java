package it.unipi.lsmsd.socialnews.dao.neo4j;


import it.unipi.lsmsd.socialnews.dao.model.neo4j.Post;

import java.util.HashMap;
import java.util.Map;

public class PostNeo4jDAO{
    private final Neo4jConnection neo4jConnection;

    public PostNeo4jDAO() {
        neo4jConnection = Neo4jConnection.getConnection();
    }

    // CREATION OPERATIONS

    public void addPost(Post post){
        String query =
                "MATCH (r:Reporter {reporter_id: $reporterId}) "+
                        "MERGE (p:Post {post_id: $postId}) " +
                        "CREATE (r) -[:WRITE]-> (p)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", post.getReporter().getReporterId());
        parameters.put("postId", post.getPostId());

        neo4jConnection.getNeo4jSession().query(Post.class, query, parameters);
    }


    // READ OPERATIONS

    public Post getPostById(String postId){
        return neo4jConnection.getNeo4jSession().load(Post.class, postId);
    }

    // DELETE OPERATIONS

    public void deletePost(String postId){
        String query = "MATCH (p:Post {post_id: $postId}) <-[rel]-() " +
                "DELETE rel "+
                "DELETE p";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("postId", postId);

        neo4jConnection.getNeo4jSession().query(Post.class, query, parameters);
    }

    public void deletePostsByReporterId(String reporterId) {
        String query = "MATCH (p:Post) <-[w:WRITE]-(r:Reporter {reporter_id: $reporterId}) "+
                "OPTIONAL MATCH (p) <-[rep:REPORT]- () "+
                "DELETE rep " +
                "DELETE w " +
                "DELETE p";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reporterId", reporterId);

        neo4jConnection.getNeo4jSession().query(Post.class, query, parameters);
    }
}
