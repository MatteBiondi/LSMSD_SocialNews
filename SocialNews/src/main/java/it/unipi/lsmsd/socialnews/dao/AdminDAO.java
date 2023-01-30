package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.mongodb.User;

public interface AdminDAO {
    User authenticate(String email, String password) throws SocialNewsDataAccessException;
}
