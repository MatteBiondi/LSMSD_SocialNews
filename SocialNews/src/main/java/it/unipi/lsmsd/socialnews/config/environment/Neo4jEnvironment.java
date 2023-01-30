package it.unipi.lsmsd.socialnews.config.environment;

public class Neo4jEnvironment {
    private static final String DEFAULT_HOST = "localhost";
    private static final Integer DEFAULT_PORT = 7687;

    private static final String DEFAULT_DATABASE = "socialnews";

    private static final String DEFAULT_USERNAME = "socialnewsadmin";

    private static final String DEFAULT_PASSWORD = "AdminPsw";

    // format: neo4j://<host>:<port>>
    // The usage of neo4j protocol to let it work both in cluster or single instance
    private static final String DEFAULT_URL_FORMAT = "neo4j://%s:%d";


    public static String getNeo4jUrl() {
        String url = System.getenv("NEO4J_URI");
        if (url == null || url.isEmpty()) {
            return String.format(DEFAULT_URL_FORMAT, DEFAULT_HOST, DEFAULT_PORT);
        }
        return url;
    }

    public static String getNeo4jDatabase() {
        String database = System.getenv("NEO4J_DATABASE");
        if (database == null || database.isEmpty()) {
            return DEFAULT_DATABASE;
        }
        return database;
    }

    public static String getNeo4jUsername() {
        String user = System.getenv("NEO4J_USERNAME");
        if (user == null || user.isEmpty()) {
            return DEFAULT_USERNAME;
        }
        return user;
    }

    public static String getNeo4jPassword() {
        String password = System.getenv("NEO4J_PASSWORD");
        if (password == null || password.isEmpty()) {
            return DEFAULT_PASSWORD;
        }
        return password;
    }
}
