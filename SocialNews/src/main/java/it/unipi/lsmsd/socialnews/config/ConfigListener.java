package it.unipi.lsmsd.socialnews.config;

import it.unipi.lsmsd.socialnews.dao.mongodb.MongoConnection;
import it.unipi.lsmsd.socialnews.dao.neo4j.Neo4jConnection;

import javax.servlet.*;
import javax.servlet.annotation.*;

@WebListener
public class ConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MongoConnection.getConnection();
        Neo4jConnection.getConnection();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MongoConnection.getConnection().close();
        Neo4jConnection.getConnection().closeConnection();
    }
}
