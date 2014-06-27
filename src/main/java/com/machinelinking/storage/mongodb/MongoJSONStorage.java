package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.DocumentConverter;
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
public class MongoJSONStorage implements JSONStorage<MongoJSONStorageConfiguration,MongoDocument, MongoSelector> {

    private final MongoJSONStorageConfiguration configuration;
    private final Mongo mongo;
    private final DB db;
    private final DocumentConverter<MongoDocument> converter;

    public MongoJSONStorage(
            MongoJSONStorageConfiguration config, DocumentConverter<MongoDocument> converter
    ) throws UnknownHostException {
        this.configuration = config;
        this.mongo  = new Mongo(config.getHost(), config.getPort());
        this.db     = mongo.getDB( config.getDB() );
        this.converter = converter;
    }

    @Override
    public MongoJSONStorageConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public DocumentConverter<MongoDocument> getConverter() {
        return converter;
    }

    @Override
    public boolean exists() {
        return exists(null);
    }

    @Override
    public boolean exists(String collection) {
        final String targetConnection = collection == null ? configuration.getCollection() : collection;
        return db.collectionExists(targetConnection);
    }

    @Override
    public JSONStorageConnection<MongoDocument, MongoSelector> openConnection() {
        return openConnection(null);
    }

    @Override
    public MongoJSONStorageConnection openConnection(String collection) {
        final String targetConnection = collection == null ? configuration.getCollection() : collection;
        return new MongoJSONStorageConnection(db.getCollection(targetConnection), converter);
    }

    @Override
    public void deleteCollection() {
        deleteCollection(null);
    }

    @Override
    public void deleteCollection(String collection) {
        final String targetConnection = collection == null ? configuration.getCollection() : collection;
        this.db.getCollection(targetConnection).drop();
    }

    public void close() {
        mongo.close();
    }

}
