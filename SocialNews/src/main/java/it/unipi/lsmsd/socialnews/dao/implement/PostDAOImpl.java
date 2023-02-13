package it.unipi.lsmsd.socialnews.dao.implement;

import com.mongodb.client.ClientSession;
import it.unipi.lsmsd.socialnews.dao.PostDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoPostDAO;
import it.unipi.lsmsd.socialnews.dao.neo4j.Neo4jPostDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.List;

public class PostDAOImpl implements PostDAO {
    private static final Logger logger = LoggerFactory.getLogger(PostDAO.class);

    private final MongoPostDAO mongoPostDAO;
    private final Neo4jPostDAO neo4jPostDAO;

    public PostDAOImpl(){
        mongoPostDAO = new MongoPostDAO();
        neo4jPostDAO = new Neo4jPostDAO();
    }

    @Override
    public String createPost(String reporterId, Post newPost) throws SocialNewsDataAccessException {
        // Insert on Neo4J is lazy
        Long modifiedDocs = mongoPostDAO.createPost(reporterId, newPost);
        if (modifiedDocs > 0)
            return newPost.getId();
        else
            throw new SocialNewsDataAccessException("Insertion failed");
    }

    @Override
    public List<Post> postsByReporterIdPrev(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoPostDAO.postsByReporterIdPrev(reporterId, offset, pageSize);
    }

    @Override
    public List<Post> postsByReporterIdNext(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoPostDAO.postsByReporterIdNext(reporterId, offset, pageSize);
    }

    @Override
    public List<Reporter> postsByHashtag(String hashtag, Integer pageSize) throws SocialNewsDataAccessException {
        return postsByHashtag(hashtag, null, pageSize);
    }

    @Override
    public List<Reporter> postsByHashtag(String hashtag, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoPostDAO.postsByHashtag(hashtag, offset, pageSize);
    }

    @Override
    public Long removePost(String reporterId, String postId) throws SocialNewsDataAccessException {
        ClientSession session = mongoPostDAO.openSession();
        Long resultMongo;
        boolean resultNeo = false;
        try{
            session.startTransaction();
            resultMongo = mongoPostDAO.removePost(session, reporterId, postId);
            resultNeo = String.valueOf(neo4jPostDAO.deletePost(postId)).equals(postId);
            session.commitTransaction();
        }
        catch (Exception ex){
            if(resultNeo){
            logger.error(String.format("Post %s of reporter %s, check consistency on databases", postId,
                    reporterId));
            }
            session.abortTransaction();
            session.close();
            throw new SocialNewsDataAccessException(ex.getMessage());
        }
        session.close();
        return resultMongo;
    }

    @Override
    public List<Post> latestHottestPosts(String reporterId, Integer nTop, Date from) throws SocialNewsDataAccessException {
        return mongoPostDAO.latestHottestPosts(reporterId, nTop, from);
    }
}
