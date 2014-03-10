package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.JSONStorage;
import com.machinelinking.storage.JSONStorageConnection;
import com.mongodb.DB;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

/**
 * Implementation of {@link com.machinelinking.storage.JSONStorage} based on <i>MongoDB</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorage implements JSONStorage<MongoJSONStorageConfiguration,MongoDocument> {

    private final MongoJSONStorageConfiguration config;
    private final Mongo mongo;
    private final DB db;

    public MongoJSONStorage(MongoJSONStorageConfiguration config) throws UnknownHostException {
        this.config = config;
        mongo       = new Mongo(config.getHost(), config.getPort());
        db          = mongo.getDB( config.getDB() );
    }

    @Override
    public MongoJSONStorageConfiguration getConfiguration() {
        return config;
    }

    @Override
    public JSONStorageConnection<MongoDocument> openConnection(String table) {
        return new MongoJSONStorageConnection(db.getCollection(table));
    }

    public void close() {
        mongo.close();
    }

}
