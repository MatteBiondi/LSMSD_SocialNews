package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Admin;

public interface AdminDAO {
    /**
     * Authenticates the admin identified by email via secret password
     *
     * @param email email of the admin
     * @param password password to compare with the one saved on database
     * @return if authentication succeed admin object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    Admin authenticate(String email, String password) throws SocialNewsDataAccessException;
}
