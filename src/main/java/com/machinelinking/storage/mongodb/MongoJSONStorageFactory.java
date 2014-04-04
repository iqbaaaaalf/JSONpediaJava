package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.AbstractJSONStorageFactory;
import com.machinelinking.storage.DocumentConverter;
import com.machinelinking.storage.JSONStorageConfiguration;

import java.net.UnknownHostException;

/**
 * Mongo implementation of {@link com.machinelinking.storage.JSONStorageFactory}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageFactory extends AbstractJSONStorageFactory {

    @Override
    public MongoJSONStorageConfiguration createConfiguration(String configURI) {
        final Configuration c = parseConfigurationURI(configURI);
        return new MongoJSONStorageConfiguration(c.host, c.port, c.db, c.collection);
    }

    @Override
    public MongoJSONStorage createStorage(
            JSONStorageConfiguration config, DocumentConverter converter
    ) {
        if(!(config instanceof MongoJSONStorageConfiguration)) throw new IllegalArgumentException();
        try {
            return new MongoJSONStorage((MongoJSONStorageConfiguration)config, converter);
        } catch (UnknownHostException uhe) {
            throw new IllegalArgumentException("Error while instantiating storage with configuration: " + config);
        }
    }

    @Override
    public MongoJSONStorage createStorage(JSONStorageConfiguration config) {
        return createStorage(config, null);
    }

}
