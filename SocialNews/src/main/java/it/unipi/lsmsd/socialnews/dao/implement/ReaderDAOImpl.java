package it.unipi.lsmsd.socialnews.dao.implement;

import it.unipi.lsmsd.socialnews.dao.ReaderDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Reader;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoReaderDAO;
import java.util.List;

public class ReaderDAOImpl implements ReaderDAO {
    private final MongoReaderDAO mongoReaderDAO;
    // private final Neo4JReaderDAO neo4jReaderDAO;

    public ReaderDAOImpl(){
        mongoReaderDAO = new MongoReaderDAO();
        // neo4jReaderDAO = new Neo4JReaderDAO();
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
        // TODO: Insert on Neo4J may be lazy
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
     * Remove a reader from the database
     *
     * @param email email of the reader to remove
     * @return number of reader removed from database
     * @throws SocialNewsDataAccessException in case of failure of the delete operation on database
     */
    @Override
    public Long removeReader(String email) throws SocialNewsDataAccessException {
        return mongoReaderDAO.removeReader(email);
        //TODO: remove from Neo4J
    }
}
