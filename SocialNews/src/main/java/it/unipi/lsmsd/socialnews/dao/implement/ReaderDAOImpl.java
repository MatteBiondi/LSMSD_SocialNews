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

    /**
     * Insert new reader into the database
     *
     * @param newReader reader object containing information of the new reader
     * @return identifier assigned to the new reader
     * @throws SocialNewsDataAccessException in case of failure of the insert operation on database
     */
    @Override
    public String register(Reader newReader) throws SocialNewsDataAccessException {
        // Insert on Neo4J is lazy
        return mongoReaderDAO.register(newReader);
    }

    /**
     * Authenticates the reader identified by email via secret password
     *
     * @param email    email of the reader
     * @param password password to compare with the one saved on database
     * @return if authentication succeed reader object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public Reader authenticate(String email, String password) throws SocialNewsDataAccessException {
        return mongoReaderDAO.authenticate(email, password);
    }

    /**
     * Retrieves information about the reader identified by email field
     *
     * @param email email of the reader
     * @return reader object containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public Reader readerByEmail(String email) throws SocialNewsDataAccessException {
        return mongoReaderDAO.readerByEmail(email);
    }

    /**
     * Retrieves information about all the readers saved on database, limiting the list size to the dimension specified
     *
     * @param pageSize number of readers to retrieve
     * @return list of reader objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reader> allReaders(Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReaderDAO.allReaders(pageSize);
    }

    /**
     * Retrieves information about all the readers saved on database, limiting the list size to the dimension specified,
     * starting from the reader specified as argument. It allows the implementation of pagination of the readers
     *
     * @param offset   reader from which the query starts to retrieve information
     * @param pageSize number of readers to retrieve
     * @return list of reader objects containing all the information
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reader> allReaders(Reader offset, Integer pageSize) throws SocialNewsDataAccessException {
        return mongoReaderDAO.allReaders(offset, pageSize);
    }

    /**
     * Remove a reader from the database and the associated comments
     *
     * @param readerId id of the reader to remove
     * @return number of reader removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
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

    /**
     * Add a following relationship between a reader and a reporter
     *
     * @param readerId id of the reader
     * @param reporterId id of the reporter
     * @return number of following relationship created
     * @throws SocialNewsDataAccessException in case of failure of the creation operation of following relation
     */
    @Override
    public Integer followReporter(String readerId, String reporterId) throws SocialNewsDataAccessException{
        return neo4jReaderDAO.followReporter(readerId, reporterId);
    }

    /**
     * Retrieve the followed reporters of a certain reader
     *
     * @param readerId id of the reader
     * @param limit maximum number of reporters to retrieve per request
     * @param offset reporter from which the query starts to retrieve information
     * @return list of reporter objects containing basic information (id, name and picture)
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reporter> getFollowingByReaderId(String readerId, int limit, int offset)
            throws SocialNewsDataAccessException{
        return neo4jReaderDAO.getFollowingByReaderId(readerId,limit,offset);
    }

    /**
     * Remove a following relationship between a reader and a reporter
     *
     * @param readerId id of the reader
     * @param reporterId id of the reporter
     * @return number of following relationship removed from the database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    @Override
    public Integer unfollowReporter(String readerId, String reporterId) throws SocialNewsDataAccessException{
        return neo4jReaderDAO.unfollowReporter(readerId, reporterId);
    }

    /**
     * Given a reader, suggest most popular reporters that are not in the reader's following
     *
     * @param readerId id of the reader
     * @param limitListLen number of suggested reporters
     * @return list of reporter objects containing basic information (id, name and picture)
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public List<Reporter> suggestReporters(String readerId, int limitListLen) throws SocialNewsDataAccessException{
        return neo4jReaderDAO.suggestReporters(readerId, limitListLen);
    }

    /**
     * Compute the number of registered readers grouped by male\female\others
     *
     * @return JSON object containing the information computed by aggregation pipeline
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public ObjectNode genderStatistic() throws SocialNewsDataAccessException {
        return mongoReaderDAO.genderStatistic();
    }

    /**
     * Compute the number of registered readers grouped by his/her nationality
     *
     * @return SON object containing the information computed by aggregation pipeline
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public ArrayNode nationalityStatistic() throws SocialNewsDataAccessException {
        return mongoReaderDAO.nationalityStatistic();
    }
}
