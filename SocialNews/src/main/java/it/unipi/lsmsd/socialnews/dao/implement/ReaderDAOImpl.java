package it.unipi.lsmsd.socialnews.dao.implement;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.ClientSession;
import it.unipi.lsmsd.socialnews.dao.ReaderDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoReaderDAO;
import it.unipi.lsmsd.socialnews.dao.neo4j.Neo4jReaderDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReaderDAOImpl implements ReaderDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReaderDAO.class);

    private final MongoReaderDAO mongoReaderDAO;
    private final Neo4jReaderDAO neo4jReaderDAO;

    public ReaderDAOImpl(){
        mongoReaderDAO = new MongoReaderDAO();
        neo4jReaderDAO = new Neo4jReaderDAO();
    }

    @Override
    public String register(Reader newReader) throws SocialNewsDataAccessException {
        // Insert on Neo4J is lazy
        return mongoReaderDAO.register(newReader);
    }

    @Override
    public Reader authenticate(String email, String password) throws SocialNewsDataAccessException {
        return mongoReaderDAO.authenticate(email, password);
    }

    @Override
    public Reader readerByEmail(String email) throws SocialNewsDataAccessException {
        return mongoReaderDAO.readerByEmail(email);
    }

    @Override
    public List<Reader> allReadersPrev(Reader filter, Reader offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReaderDAO.allReadersPrev(filter, offset, pageSize);
    }

    @Override
    public List<Reader> allReadersNext(Reader filter, Reader offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReaderDAO.allReadersNext(filter, offset, pageSize);
    }

    @Override
    public Long removeReader(String readerId) throws SocialNewsDataAccessException {
        ClientSession session = mongoReaderDAO.openSession();
        Long resultMongo;
        boolean resultNeo = false;
        try {
            session.startTransaction();
            resultMongo = mongoReaderDAO.removeReader(session, readerId);
            resultNeo = String.valueOf(neo4jReaderDAO.deleteReader(readerId)).equals(readerId);
            session.commitTransaction();
        }
        catch (Exception ex){
            if(resultNeo){
                logger.error(String.format("Reader %s, check consistency on databases", readerId));
            }
            session.abortTransaction();
            session.close();
            throw new SocialNewsDataAccessException(ex.getMessage());
        }
        session.close();

        return resultMongo;
    }

    @Override
    public Integer followReporter(String readerId, String reporterId) throws SocialNewsDataAccessException{
        return neo4jReaderDAO.followReporter(readerId, reporterId);
    }

    @Override
    public List<Reporter> getFollowingByReaderId(String readerId, int limit, int offset)
            throws SocialNewsDataAccessException{
        return neo4jReaderDAO.getFollowingByReaderId(readerId,limit,offset);
    }

    @Override
    public Integer unfollowReporter(String readerId, String reporterId) throws SocialNewsDataAccessException{
        return neo4jReaderDAO.unfollowReporter(readerId, reporterId);
    }

    @Override
    public List<Reporter> suggestReporters(String readerId, int limitListLen) throws SocialNewsDataAccessException{
        return neo4jReaderDAO.suggestReporters(readerId, limitListLen);
    }

    @Override
    public ObjectNode genderStatistic() throws SocialNewsDataAccessException {
        return mongoReaderDAO.genderStatistic();
    }

    @Override
    public ArrayNode nationalityStatistic() throws SocialNewsDataAccessException {
        return mongoReaderDAO.nationalityStatistic();
    }
}
