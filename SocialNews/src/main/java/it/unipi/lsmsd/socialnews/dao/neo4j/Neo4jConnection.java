package it.unipi.lsmsd.socialnews.dao.neo4j;

import it.unipi.lsmsd.socialnews.environment.Neo4jEnvironment;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public abstract class Neo4jConnection{
    protected final SessionFactory sessionFactory;
    private final String ENTITIES_PACKAGE = "it.unipi.lsmsd.socialnews.model.neo4j";
    public Neo4jConnection() {
        String uri = Neo4jEnvironment.getNeo4jUrl();
        String username = Neo4jEnvironment.getNeo4jUsername();
        String password = Neo4jEnvironment.getNeo4jPassword();
        String database = Neo4jEnvironment.getNeo4jDatabase();

        Configuration config = new Configuration.Builder()
                .uri(uri)
                .database(database)
                .credentials(username, password)
                .build();

        this.sessionFactory = new SessionFactory(config, ENTITIES_PACKAGE);
    }

    public Session getNeo4jSession() {
        return sessionFactory.openSession();
    }
}
