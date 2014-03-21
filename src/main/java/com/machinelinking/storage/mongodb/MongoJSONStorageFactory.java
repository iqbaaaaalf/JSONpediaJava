package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.JSONStorageConfiguration;
import com.machinelinking.storage.JSONStorageFactory;

import java.net.UnknownHostException;

/**
 * Mongo implementation of {@link com.machinelinking.storage.JSONStorageFactory}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageFactory implements JSONStorageFactory {

    @Override
    public MongoJSONStorageConfiguration createConfiguration(String configURI) {
        final String[] sections = configURI.split(":");
        if(sections.length != 3) throw new IllegalArgumentException("Invalid config URI: must be: <host>:<port>:<db>");
        final String host = sections[0];
        final int port;
        try {
            port = Integer.parseInt(sections[1]);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid port, must be a number: " + sections[1]);
        }
        final String db = sections[2];
        return new MongoJSONStorageConfiguration(host, port, db);
    }

    @Override
    public MongoJSONStorage createStorage(JSONStorageConfiguration config) {
        if(!(config instanceof MongoJSONStorageConfiguration)) throw new IllegalArgumentException();
        try {
            return new MongoJSONStorage((MongoJSONStorageConfiguration)config);
        } catch (UnknownHostException uhe) {
            throw new IllegalArgumentException("Error while instantiating storage with configuration: " + config);
        }
    }

}
