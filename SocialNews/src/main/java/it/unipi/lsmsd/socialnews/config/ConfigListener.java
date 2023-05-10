package it.unipi.lsmsd.socialnews.config;

import it.unipi.lsmsd.socialnews.dao.mongodb.MongoConnection;
import it.unipi.lsmsd.socialnews.dao.neo4j.Neo4jConnection;
import it.unipi.lsmsd.socialnews.dao.redundancy.RedundancyUpdater;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.threading.ServiceWorkerPool;
import it.unipi.lsmsd.socialnews.service.util.Statistic;
import it.unipi.lsmsd.socialnews.service.util.Util;
import it.unipi.lsmsd.socialnews.servlet.admin.UsersServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The ConfigListener class is linked to events related to the application lifetime and implements two methods that will
 * be called at startup and shutdown of the web-application deployed on application server. This is particularly
 * useful to perform initialization and clean up operations.
 */
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
        UsersServlet.setPageSize(Integer.valueOf(properties.getProperty("listUserPageSize")));
        Statistic.configure(
                Integer.valueOf(properties.getProperty("defaultWindowSize")),
                Integer.valueOf(properties.getProperty("defaultLastN")),
                properties.getProperty("defaultUnitOfTime")
        );
        ServiceWorkerPool.getPool();
        RedundancyUpdater.getInstance();
        logger.info("Configuration complete");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            ServiceWorkerPool.getPool().shutdown();
        } catch (SocialNewsServiceException ex) {
            ex.printStackTrace();
            logger.error("Error during shutdown: " + ex.getMessage());
        }
        RedundancyUpdater.getInstance().stopRedundanciesUpdate();
        MongoConnection.getConnection().close();
        Neo4jConnection.getConnection().close();
        logger.info("Shutdown complete");
    }
}
