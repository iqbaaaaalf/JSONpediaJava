package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.AbstractJSONStorageLoaderTest;
import com.machinelinking.storage.JSONStorage;

import java.net.UnknownHostException;

/**
 * {@link com.machinelinking.storage.mongodb.MongoJSONStorage} test.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageLoaderTest extends AbstractJSONStorageLoaderTest {

    @Override
    protected JSONStorage getJSONStorage() throws UnknownHostException {
        return new MongoJSONStorage(
                new MongoJSONStorageConfiguration("127.0.0.1", 7654, "jsonpedia-test", "en"),
                null
        );
    }

}
