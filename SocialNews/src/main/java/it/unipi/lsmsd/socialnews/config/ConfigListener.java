package it.unipi.lsmsd.socialnews.config;

import it.unipi.lsmsd.socialnews.dao.mongodb.MongoConnection;
import it.unipi.lsmsd.socialnews.dao.neo4j.Neo4jConnection;
import it.unipi.lsmsd.socialnews.service.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class ConfigListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ServletContextListener.class);
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MongoConnection.getConnection();
        Neo4jConnection.getConnection();

        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("service.properties");
        try {
            properties.load(inputStream);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load properties");
        }
        Util.configure(properties);

        logger.info("Configuration complete");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MongoConnection.getConnection().close();
        Neo4jConnection.getConnection().close();
    }
}
