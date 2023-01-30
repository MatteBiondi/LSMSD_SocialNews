package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reader;
import org.bson.types.ObjectId;

import java.util.List;

public interface ReaderDAO {
    String register(Reader newReader) throws SocialNewsDataAccessException;
    Reader authenticate(String email, String password) throws SocialNewsDataAccessException;
    Reader findReader(String email) throws SocialNewsDataAccessException;
    List<Reader> allReaders(ObjectId offset, int pageSize) throws SocialNewsDataAccessException;
    Long removeReader(String email) throws SocialNewsDataAccessException;
}
