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
        if(sections.length != 4)
            throw new IllegalArgumentException("Invalid config URI: must be: <host>:<port>:<db>:<collection>");
        final String host = checkValid(sections[0], "host");
        final int port;
        try {
            port = Integer.parseInt(sections[1]);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid port, must be a number: " + sections[1]);
        }
        final String db = checkValid(sections[2], "db");
        final String collection = checkValid(sections[3], "collection");
        return new MongoJSONStorageConfiguration(host, port, db, collection);
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

    private String checkValid(String in, String desc) {
        if(in.trim().length() == 0) {
            throw new IllegalArgumentException(String.format("Invalid value '%s' for %s", in, desc));
        }
        return in;
    }

}
