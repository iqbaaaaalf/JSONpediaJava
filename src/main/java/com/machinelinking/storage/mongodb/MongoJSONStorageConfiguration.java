package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.JSONStorageConfiguration;

/**
 * Implementation of {@link com.machinelinking.storage.JSONStorageConfiguration} for <i>MongoDB</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageConfiguration implements JSONStorageConfiguration {

    private final String host;
    private final int    port;
    private final String db;
    private final String collection;

    public MongoJSONStorageConfiguration(String host, int port, String db, String collection) {
        this.host = host;
        this.port = port;
        this.db = db;
        this.collection = collection;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String getDB() {
        return db;
    }

    @Override
    public String getCollection() { return collection; }

}
