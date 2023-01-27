package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public abstract class MongoDAO<T>{
    private final MongoConnection mongoConnection;
    private final Class<T> template;
    private final String collectionName;
    private static final String databaseName = "socialNewsDB";

    protected MongoDAO(String collectionName, Class<T> template){
        this.template = template;
        this.collectionName = collectionName;
        this.mongoConnection = MongoConnection.getConnection();
    }

    protected MongoCollection<T> getCollection(){
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        MongoDatabase database = mongoConnection.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);

        return database.getCollection(collectionName, template);
    }
}
