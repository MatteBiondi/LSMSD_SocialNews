package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.Reader;
import java.util.List;

public interface ReaderDAO {
    String register(Reader newReader) throws SocialNewsDataAccessException;
    Reader authenticate(String email, String password) throws SocialNewsDataAccessException;
    Reader readerByEmail(String email) throws SocialNewsDataAccessException;
    List<Reader> allReaders(Integer pageSize) throws SocialNewsDataAccessException;
    List<Reader> allReaders(Reader offset, Integer pageSize) throws SocialNewsDataAccessException;
    Long removeReader(String email) throws SocialNewsDataAccessException;
}
