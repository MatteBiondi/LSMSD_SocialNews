package it.unipi.lsmsd.socialnews.dao.implement;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.ClientSession;
import it.unipi.lsmsd.socialnews.dao.ReporterDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Post;
import it.unipi.lsmsd.socialnews.dao.model.Reporter;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoReporterDAO;
import it.unipi.lsmsd.socialnews.dao.neo4j.Neo4jReporterDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class ReporterDAOImpl implements ReporterDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReporterDAO.class);

    private final MongoReporterDAO mongoReporterDAO;
    private final Neo4jReporterDAO neo4jReporterDAO;

    public ReporterDAOImpl(){
        mongoReporterDAO = new MongoReporterDAO();
        neo4jReporterDAO = new Neo4jReporterDAO();
    }


    @Override
    public String register(Reporter newReporter) throws SocialNewsDataAccessException {
        ClientSession session = mongoReporterDAO.openSession();
        boolean resultNeo = false;

        try {
            session.startTransaction();
            mongoReporterDAO.register(session, newReporter);
            resultNeo = neo4jReporterDAO.addReporter(newReporter) != null;
            session.commitTransaction();
        }
        catch (Exception ex){
            if(resultNeo){
                logger.error(String.format("Reporter %s, check consistency on databases", newReporter.getReporterId()));
            }
            session.abortTransaction();
            session.close();
            throw new SocialNewsDataAccessException(ex.getMessage());
        }
        session.close();

        return newReporter.getReporterId();
    }

    @Override
    public Reporter authenticate(String email, String password) throws SocialNewsDataAccessException {
        return mongoReporterDAO.authenticate(email, password);
    }

    @Override
    public Reporter reporterByEmail(String email) throws SocialNewsDataAccessException {
        return mongoReporterDAO.reporterByEmail(email);
    }

    @Override
    public Reporter reporterByReporterId(String reporterId, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.reporterByReporterId(reporterId, pageSize);
    }

    @Override
    public List<Reporter> reportersByFullNamePrev(String fullNamePattern, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.reportersByFullNamePrev(fullNamePattern, offset, pageSize);
    }

    @Override
    public List<Reporter> reportersByFullNameNext(String fullNamePattern, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.reportersByFullNameNext(fullNamePattern, offset, pageSize);
    }

    @Override
    public List<Reporter> allReportersPrev(Reporter filter, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.allReportersPrev(filter, offset, pageSize);
    }

    @Override
    public List<Reporter> allReportersNext(Reporter filter, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.allReportersNext(filter, offset, pageSize);
    }

    @Override
    public Long removeReporter(String reporterId) throws SocialNewsDataAccessException {
        ClientSession session = mongoReporterDAO.openSession();
        Long resultMongo;
        boolean resultNeo = false;
        try {
            session.startTransaction();
            resultMongo = mongoReporterDAO.removeReporter(session, reporterId); //TODO: remove comments
            resultNeo = String.valueOf(neo4jReporterDAO.deleteReporter(reporterId)).equals(reporterId);
            session.commitTransaction();
        }
        catch (Exception ex){
            if(resultNeo){
                logger.error(String.format("Reporter %s, check consistency on databases", reporterId));
            }
            session.abortTransaction();
            session.close();
            throw new SocialNewsDataAccessException(ex.getMessage());
        }
        session.close();

        return resultMongo;
    }

    @Override
    public Integer getNumOfFollowers(String reporterId) throws SocialNewsDataAccessException{
        return neo4jReporterDAO.getNumOfFollowers(reporterId);
    }

    @Override
    public ObjectNode getNumOfFollowers(String reporterId, String readerId) throws SocialNewsDataAccessException{
        return neo4jReporterDAO.getNumOfFollowers(reporterId,readerId);
    }

    @Override
    public ArrayNode getMostPopularReporters(int limitTopRanking) throws SocialNewsDataAccessException{
        return neo4jReporterDAO.getMostPopularReporters(limitTopRanking);
    }

    @Override
    public Boolean checkAndSwap(String email) throws SocialNewsDataAccessException {
        return mongoReporterDAO.checkAndSwapDocument(email);
    }
}
