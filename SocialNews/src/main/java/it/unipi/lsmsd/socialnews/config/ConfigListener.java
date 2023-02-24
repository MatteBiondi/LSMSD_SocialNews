package it.unipi.lsmsd.socialnews.config;

import it.unipi.lsmsd.socialnews.dao.mongodb.MongoConnection;
import it.unipi.lsmsd.socialnews.dao.neo4j.Neo4jConnection;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.util.ServiceWorkerPool;
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
        logger.info("Configuration complete");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MongoConnection.getConnection().close();
        Neo4jConnection.getConnection().close();
        try {
            ServiceWorkerPool.getPool().shutdown();
        } catch (SocialNewsServiceException ex) {
            ex.printStackTrace();
            logger.error("Error during shutdown: " + ex.getMessage());
        }
        logger.info("Shutdown complete");
    }
}
