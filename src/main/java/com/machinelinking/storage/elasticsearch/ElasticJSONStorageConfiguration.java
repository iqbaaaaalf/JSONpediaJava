package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.JSONStorageConfiguration;

/**
 * {@link com.machinelinking.storage.JSONStorageConfiguration} implementation for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticJSONStorageConfiguration implements JSONStorageConfiguration {

    private final String host;
    private final int port;
    private final String db;
    private final String collection;

    public ElasticJSONStorageConfiguration(String host, int port, String db, String collection) {
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
    public String getCollection() {
        return collection;
    }

}
