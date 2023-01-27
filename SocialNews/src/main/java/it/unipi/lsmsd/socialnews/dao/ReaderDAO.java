package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.model.Reader;

public interface ReaderDAO {
    String register(Reader newReader);
    Reader authenticate(String email, String password);
}
