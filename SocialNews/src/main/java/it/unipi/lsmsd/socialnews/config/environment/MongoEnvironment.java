package it.unipi.lsmsd.socialnews.config.environment;

public final class MongoEnvironment {
    private static final String DEFAULT_PROTOCOL = "mongodb";
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "root";
    private static final String DEFAULT_HOSTNAME = "localhost:27019";
    private static final String DEFAULT_OPTS = "";
    private static final String DEFAULT_DATABASE = "socialNewsDB";
    private static final String DEFAULT_URI_FORMAT = "%s://%s:%s@%s/%s?";// <protocol>://<username>:<password>@<hostname>/?<options>

    /**
     * Private constructor to prevent instantiation
     */
    private MongoEnvironment(){ }

    private static String getMongoProtocol() {
        String protocol = System.getenv("MONGO_PROTOCOL");
        if (protocol == null || protocol.isEmpty()) {
            return DEFAULT_PROTOCOL;
        }
        return protocol;
    }

    private static String getMongoUsername() {
        String username = System.getenv("MONGO_USERNAME");
        if (username == null || username.isEmpty()) {
            return DEFAULT_USERNAME;
        }
        return username;
    }

    private static String getMongoPassword() {
        String password = System.getenv("MONGO_PASSWORD");
        if (password == null || password.isEmpty()) {
            return DEFAULT_PASSWORD;
        }
        return password;
    }

    private static String getMongoHostname() {
        String hostname = System.getenv("MONGO_HOSTNAME");
        if (hostname == null || hostname.isEmpty()) {
            String host = System.getenv("MONGO_HOST");
            String port = System.getenv("MONGO_PORT");
            if (host == null || host.isEmpty() || port == null || port.isEmpty()){
                return DEFAULT_HOSTNAME;
            }
            return String.format("%s:%s", host,port);
        }
        return hostname;
    }

    private static String getMongoOptions() {
        String options = System.getenv("MONGO_OPTIONS");//TODO: write\read concern, replicas\sharding config
        if (options == null || options.isEmpty()) {
            return DEFAULT_OPTS;
        }
        return options;
    }

    public static String getMongoURI() {
        return String.format(
                DEFAULT_URI_FORMAT,
                getMongoProtocol(),
                getMongoUsername(),
                getMongoPassword(),
                getMongoHostname(),
                getMongoOptions()
        );
    }

    public static String getMongoDatabase() {
        String database = System.getenv("MONGO_DATABASE");
        if (database == null || database.isEmpty()) {
            return DEFAULT_DATABASE;
        }
        return database;
    }

}
