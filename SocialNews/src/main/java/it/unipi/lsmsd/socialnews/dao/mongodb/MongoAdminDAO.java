package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.model.Admin;
import it.unipi.lsmsd.socialnews.model.User;

public class MongoAdminDAO extends MongoDAO<Admin> {

    public MongoAdminDAO() {
        super("users", Admin.class);
    }

    public User authenticate(String email, String password) throws SocialNewsDataAccessException {
        try{
            return getCollection()
                    .find(Filters.and(
                            Filters.eq("email", email),
                            Filters.eq("password", password),
                            Filters.exists("isAdmin", true)))
                    .first();
        }
        catch (MongoException me){
            me.printStackTrace();
            throw new SocialNewsDataAccessException("Query failed: " + me.getMessage());
        }
    }
}
