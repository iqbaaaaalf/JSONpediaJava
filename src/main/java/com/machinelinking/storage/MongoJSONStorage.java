package com.machinelinking.storage;

import com.mongodb.DB;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorage implements JSONStorage<MongoJSONStorageConfiguration,DBObjectDocument> {

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
    public JSONStorageConnection<DBObjectDocument> openConnection(String table) {
        return new MongoJSONStorageConnection(db.getCollection(table));
    }

    public void close() {
        mongo.close();
    }

}
