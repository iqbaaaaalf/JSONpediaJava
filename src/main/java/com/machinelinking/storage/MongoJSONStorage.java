package com.machinelinking.storage;

import com.mongodb.DB;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorage implements JSONStorage<DBObjectDocument> {

    private final Mongo mongo;
    private final DB db;

    public MongoJSONStorage() throws UnknownHostException {
        mongo = new Mongo( "127.0.0.1", 7654);
        db = mongo.getDB("jsonpedia");
    }

    @Override
    public JSONStorageConnection<DBObjectDocument> openConnection(String table) {
        return new MongoJSONStorageConnection(db.getCollection(table));
    }

    public void close() {
        mongo.close();
    }

}
