package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoConnection {
    private static volatile MongoConnection instance = null;
    private final MongoClient mongoClient;

    private MongoConnection(){
        // Connection string //TODO: write\read concern, replicas\sharding config
        ConnectionString connectionString = new ConnectionString(
                ""
        );

        // Connection settings
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)    //Connection string
                .serverApi(ServerApi                        //Stable API V1
                        .builder()
                        .version(ServerApiVersion.V1)
                        .strict(true)
                        .deprecationErrors(true)
                        .build())
                .build();

        mongoClient = MongoClients.create(settings);

        System.out.println("New MongoClient instance built!");
    }

    public static MongoConnection getConnection(){
        if(instance == null){
            synchronized (MongoConnection.class){
                if(instance == null){
                    instance = new MongoConnection();
                }
            }
        }
        return instance;
    }

    public void closeConnection(){
        if(instance != null){
            synchronized (MongoConnection.class){
                if(instance != null){
                    mongoClient.close(); // Method is guaranteed to be thread safe by MongoDB documentation
                    instance = null;
                    System.out.println("MongoClient instance destroyed!");
                }
            }
        }
    }

    public MongoDatabase getDatabase (String database){
        return mongoClient.getDatabase(database);
    }

    public boolean ping(){
        MongoDatabase database = mongoClient.getDatabase("socialNewsDB");
        try{
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = database.runCommand(command);
            if(commandResult.containsKey("ok") && commandResult.getInteger("ok") == 1)
                return true;
        }
        catch (MongoException ex){
            ex.printStackTrace();
        }
        return false;
    }
}
