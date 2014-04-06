package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.DocumentConverter;
import com.machinelinking.storage.JSONStorage;
import com.mongodb.DB;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

/**
 * Implementation of {@link com.machinelinking.storage.JSONStorage} based on <i>MongoDB</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorage implements JSONStorage<MongoJSONStorageConfiguration,MongoDocument, MongoSelector> {

    private final MongoJSONStorageConfiguration config;
    private final Mongo mongo;
    private final DB db;
    private final DocumentConverter<MongoDocument> converter;

    public MongoJSONStorage(
            MongoJSONStorageConfiguration config, DocumentConverter<MongoDocument> converter
    ) throws UnknownHostException {
        this.config = config;
        this.mongo  = new Mongo(config.getHost(), config.getPort());
        this.db     = mongo.getDB( config.getDB() );
        this.converter = converter;
    }

    @Override
    public MongoJSONStorageConfiguration getConfiguration() {
        return config;
    }

    @Override
    public DocumentConverter<MongoDocument> getConverter() {
        return converter;
    }

    @Override
    public MongoJSONStorageConnection openConnection(String collection) {
        return new MongoJSONStorageConnection(db.getCollection(collection), converter);
    }

    @Override
    public void deleteCollection(String collection) {
        this.db.getCollection(collection).drop();
    }

    public void close() {
        mongo.close();
    }

}
