package it.unipi.lsmsd.socialnews.dao.implement;

import it.unipi.lsmsd.socialnews.dao.AdminDAO;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.model.Admin;
import it.unipi.lsmsd.socialnews.dao.mongodb.MongoAdminDAO;

public class AdminDAOImpl implements AdminDAO {

    private final MongoAdminDAO mongoAdminDAO;

    public AdminDAOImpl(){
       mongoAdminDAO = new MongoAdminDAO();
    }

    /**
     * Authenticates the admin identified by email via secret password
     *
     * @param email email of the admin
     * @param password password to compare with the one saved on database
     * @return if authentication succeed admin object containing all the information, <b>null</b> otherwise
     * @throws SocialNewsDataAccessException in case of failure of the query operation on database
     */
    @Override
    public Admin authenticate(String email, String password) throws SocialNewsDataAccessException {
        return mongoAdminDAO.authenticate(email, password);
    }
}
