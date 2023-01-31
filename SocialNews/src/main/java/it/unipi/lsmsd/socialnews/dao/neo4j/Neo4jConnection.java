package it.unipi.lsmsd.socialnews.dao.neo4j;

import it.unipi.lsmsd.socialnews.config.environment.Neo4jEnvironment;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Neo4jConnection{
    private static final Logger logger = LoggerFactory.getLogger(Neo4jConnection.class);
    private static final Configuration config = new Configuration.Builder()
            .uri(Neo4jEnvironment.getNeo4jUrl())
            .database(Neo4jEnvironment.getNeo4jDatabase())
            .credentials(Neo4jEnvironment.getNeo4jUsername(), Neo4jEnvironment.getNeo4jPassword())
            .build();
    private static final String ENTITIES_PACKAGE = "it.unipi.lsmsd.socialnews.dao.model.neo4j";
    private static final SessionFactory sessionFactory = new SessionFactory(config, ENTITIES_PACKAGE);

    private static final Neo4jConnection connection = new Neo4jConnection();

    private Neo4jConnection() {
    }

    public static Neo4jConnection getConnection() {
        logger.info("Neo4j connection established");
        return connection;
    }

    public void closeConnection() {
        logger.info("Neo4j connection closed");
        sessionFactory.close();
    }

    public Session getNeo4jSession() {
        logger.info("New Neo4j session opened");
        return sessionFactory.openSession();
    }
}
