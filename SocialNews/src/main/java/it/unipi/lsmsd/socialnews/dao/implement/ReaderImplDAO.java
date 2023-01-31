package it.unipi.lsmsd.socialnews.dao.implement;

import it.unipi.lsmsd.socialnews.dao.ReaderDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reader;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoReaderDAO;

import java.util.List;

public class ReaderImplDAO implements ReaderDAO {

    @Override
    public String register(Reader newReader) throws SocialNewsDataAccessException {
        return null;
    }

    @Override
    public Reader authenticate(String email, String password) throws SocialNewsDataAccessException {
        return null;
    }

    @Override
    public Reader readerByEmail(String email) throws SocialNewsDataAccessException {
        return new MongoReaderDAO().readerByEmail(email);
    }

    @Override
    public List<Reader> allReaders(Integer pageSize) throws SocialNewsDataAccessException {
        return null;
    }

    @Override
    public List<Reader> allReaders(Reader offset, Integer pageSize) throws SocialNewsDataAccessException {
        return null;
    }

    @Override
    public Long removeReader(String email) throws SocialNewsDataAccessException {
        return null;
    }
}
