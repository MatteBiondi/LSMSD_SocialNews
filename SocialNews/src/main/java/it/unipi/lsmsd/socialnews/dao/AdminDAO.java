package it.unipi.lsmsd.socialnews.dao;

import it.unipi.lsmsd.socialnews.model.User;

public interface AdminDAO {
    User authenticate(String email, String password);
}
