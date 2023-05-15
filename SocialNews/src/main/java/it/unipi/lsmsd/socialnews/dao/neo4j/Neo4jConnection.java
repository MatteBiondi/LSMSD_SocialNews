package it.unipi.lsmsd.socialnews.dao.neo4j;

import it.unipi.lsmsd.socialnews.config.environment.Neo4jEnvironment;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Neo4jConnection{
    private static final Logger logger = LoggerFactory.getLogger(Neo4jConnection.class);
    private final Driver driver;

    private static volatile Neo4jConnection connection = null;

    private Neo4jConnection() {
        driver = GraphDatabase.driver(Neo4jEnvironment.getNeo4jUrl(),
                AuthTokens.basic(Neo4jEnvironment.getNeo4jUsername(), Neo4jEnvironment.getNeo4jPassword()));

        logger.info("Neo4j instance created");
    }

    public static Neo4jConnection getConnection() {
        if (connection == null){
            synchronized (Neo4jConnection.class){
                if (connection == null) {
                    connection = new Neo4jConnection();
                }
            }
        }
        return connection;
    }

    public void close() {
        logger.info("Neo4j connection closed");
        driver.close();
    }

    public Session getNeo4jSession() {
        return driver.session(SessionConfig.forDatabase(Neo4jEnvironment.getNeo4jDatabase()));
    }

    public void ping(){
        driver.verifyConnectivity();
    }
}
