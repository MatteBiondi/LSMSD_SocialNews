package it.unipi.lsmsd.socialnews.config.environment;

public final class MongoEnvironment {

    private static final String DEFAULT_PROTOCOL = "mongodb";
    private static final String DEFAULT_USERNAME = "socialnews";
    private static final String DEFAULT_PASSWORD = "root";
    private static final String DEFAULT_HOSTNAME = "172.16.5.20:27017,172.16.5.21:27017,172.16.5.22:27017";
    private static final String DEFAULT_DATABASE = "socialNewsDB";
    private static final String DEFAULT_OPTS = "authSource=admin&replicaSet=socialNews&appname=SocialNewsWebapp&ssl" +
            "=false&readPreference=nearest";
    // <protocol>://<username>:<password>@<hostname>/defaultDB/?<options>
    private static final String DEFAULT_URI_FORMAT = "%s://%s:%s@%s/%s?%s";

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
        String options = System.getenv("MONGO_OPTIONS");
        if (options == null || options.isEmpty()) {
            return DEFAULT_OPTS;
        }
        return options;
    }

    public static String getMongoURI() {
        return String
                .format(
                DEFAULT_URI_FORMAT,
                getMongoProtocol(),
                getMongoUsername(),
                getMongoPassword(),
                getMongoHostname(),
                getMongoDatabase(),
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
