package it.unipi.lsmsd.socialnews.dao.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.*;
import org.bson.types.ObjectId;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.*;

public abstract class MongoDAO<T>{
    private static final String databaseName = "socialNewsDB";

    private final MongoConnection mongoConnection;
    private final Class<T> template;
    private final String collectionName;

    protected MongoDAO(String collectionName, Class<T> template){
        this.template = template;
        this.collectionName = collectionName;
        this.mongoConnection = MongoConnection.getConnection();
    }

    protected MongoCollection<T> getCollection(){

        CodecProvider pojoCodecProvider = PojoCodecProvider
                .builder()
                .register("it.unipi.lsmsd.socialnews.model.mongodb")
                .register(ClassModel.builder(template)
                        .idGenerator(new IdGenerator<String>() {
                            @Override
                            public String generate() {
                                return new ObjectId().toString();
                            }

                            @Override
                            public Class<String> getType() {
                                return String.class;
                            }})
                        .build())
                .build();

        CodecRegistry pojoCodecRegistry = fromRegistries(
                getDefaultCodecRegistry(),
                fromProviders(pojoCodecProvider)
        );

        MongoDatabase database = mongoConnection.getDatabase(databaseName)
                .withCodecRegistry(pojoCodecRegistry);

        return database.getCollection(collectionName, template);
    }
}


