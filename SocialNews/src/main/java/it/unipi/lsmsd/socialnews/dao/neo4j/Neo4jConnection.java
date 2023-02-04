package it.unipi.lsmsd.socialnews.dao.neo4j;

import it.unipi.lsmsd.socialnews.config.environment.Neo4jEnvironment;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Neo4jConnection implements AutoCloseable{
    private static final Logger logger = LoggerFactory.getLogger(Neo4jConnection.class);
    private final Driver driver;

    private static final Neo4jConnection connection = new Neo4jConnection();

    private Neo4jConnection() {
        driver = GraphDatabase.driver(Neo4jEnvironment.getNeo4jUrl(),
                AuthTokens.basic(Neo4jEnvironment.getNeo4jUsername(), Neo4jEnvironment.getNeo4jPassword()));

        logger.info("Neo4j instance created");
    }

    public static Neo4jConnection getConnection() {
        logger.info("Neo4j connection established");
        return connection;
    }

    @Override
    public void close() {
        logger.info("Neo4j connection closed");
        driver.close();
    }

    public Session getNeo4jSession() {
        logger.info("New Neo4j session opened");
        return driver.session(SessionConfig.forDatabase(Neo4jEnvironment.getNeo4jDatabase()));
    }
}
