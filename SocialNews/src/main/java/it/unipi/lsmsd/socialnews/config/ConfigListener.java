package it.unipi.lsmsd.socialnews.config;

import it.unipi.lsmsd.socialnews.dao.mongodb.MongoConnection;

import javax.servlet.*;
import javax.servlet.annotation.*;

@WebListener
public class ConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MongoConnection.getConnection();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MongoConnection.getConnection().close();
    }
}
