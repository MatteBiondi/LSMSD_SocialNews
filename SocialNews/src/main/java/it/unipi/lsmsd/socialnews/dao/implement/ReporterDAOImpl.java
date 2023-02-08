package it.unipi.lsmsd.socialnews.dao.implement;

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


    /**
     * Insert new reporter into the database
     *
     * @param newReporter reporter object containing information of the new reporter
     * @return identifier assigned to the new reporter
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
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

    /**
     * Authenticates the reporter identified by email via secret password
     *
     * @param email    email of the reporter
     * @param password password to compare with the one saved on database
     * @return if authentication succeed reporter object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public Reporter authenticate(String email, String password) throws SocialNewsDataAccessException {
        return mongoReporterDAO.authenticate(email, password);
    }

    /**
     * Retrieves information about the reporter identified by email field
     *
     * @param email email of the reporter
     * @return reporter object containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public Reporter reporterByEmail(String email) throws SocialNewsDataAccessException {
        return mongoReporterDAO.reporterByEmail(email);
    }

    /**
     * Retrieves information about a reporter, including all his\her posts, limiting the post list size to the
     * dimension specified
     *
     * @param reporterId reporter identifier
     * @param pageSize   number of posts to retrieve
     * @return reporter objects containing all the information, including posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public Reporter reporterByReporterId(String reporterId, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.reporterByReporterId(reporterId, pageSize);
    }

    /**
     * Retrieves information about a reporter, including all his\her posts, limiting the post list size to the
     * dimension specified, starting to the posts specified as argument. It allows the implementation of pagination
     * of the posts
     *
     * @param reporterId reporter identifier
     * @param offset     post from which the query starts to retrieve information
     * @param pageSize   number of posts to retrieve
     * @return reporter objects containing all the information, including posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public Reporter reporterByReporterId(String reporterId, Post offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.reporterByReporterId(reporterId, offset, pageSize);
    }

    /**
     * Retrieves information about all the reporters saved on database that matches the fullName specified, excluding
     * posts, limiting the list size to the dimension specified
     *
     * @param fullNamePattern full name regex pattern, matches all full names that contains a prefix in any of its word
     * @param pageSize        number of reporters to retrieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reporter> reportersByFullName(String fullNamePattern, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.reportersByFullName(fullNamePattern, pageSize);
    }

    /**
     * Retrieves information about all the reporter saved on database that matches the fullName specified, excluding
     * posts, limiting the list size to the dimension specified, starting from the reporter specified as argument.
     * It allows the implementation of pagination of the reporters
     *
     * @param fullNamePattern full name regex pattern, matches all full names that contains a prefix in any of its word
     * @param offset          reporter from which the query starts to retrieve information
     * @param pageSize        number of reporters to retrieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reporter> reportersByFullName(String fullNamePattern, Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.reportersByFullName(fullNamePattern, offset, pageSize);
    }

    /**
     * Retrieves information about all the reporters saved on database, excluding posts, limiting the list size to the
     * dimension specified
     *
     * @param pageSize number of reporters to retrieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reporter> allReporters(Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.allReporters(pageSize);
    }

    /**
     * Retrieves information about all the reporter saved on database, excluding posts, limiting the list size to the
     * dimension specified, starting from the reporter specified as argument. It allows the implementation of
     * pagination of the reporters
     *
     * @param offset   reporter from which the query starts to retrieve information
     * @param pageSize ieve
     * @return list of reporter objects containing all the information, excluding posts
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reporter> allReporters(Reporter offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReporterDAO.allReporters(offset, pageSize);
    }

    /**
     * Remove a reporter, all posts and associated comments from the database
     *
     * @param reporterId reporter identifier
     * @return number of reporter removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    @Override
    public Long removeReporter(String reporterId) throws SocialNewsDataAccessException {
        ClientSession session = mongoReporterDAO.openSession();
        Long resultMongo;
        boolean resultNeo = false;
        try {
            session.startTransaction();
            resultMongo = mongoReporterDAO.removeReporter(session, reporterId);
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

    /**
     * Retrieve the number of followers for a given reporter
     *
     * @param reporterId id of the reporter
     * @return number of follower for the reporter identified by 'reporterId'
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public Integer getNumOfFollowers(String reporterId) throws SocialNewsDataAccessException{
        return neo4jReporterDAO.getNumOfFollowers(reporterId);
    }

    /**
     * Retrieve the most popular reporters. Popularity is given by the number of followers
     *
     * @param limitTopRanking number of retrieved reporters
     * @return list of reporter objects containing basic information (id, name and picture)
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reporter> getMostPopularReporters(int limitTopRanking) throws SocialNewsDataAccessException{
        return neo4jReporterDAO.getMostPopularReporters(limitTopRanking);
    }
}
