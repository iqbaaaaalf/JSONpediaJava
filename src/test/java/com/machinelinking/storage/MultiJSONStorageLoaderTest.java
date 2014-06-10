package com.machinelinking.storage;

import java.net.UnknownHostException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MultiJSONStorageLoaderTest extends AbstractJSONStorageLoaderTest {

    public static final String CONFIG_URI =
            "com.machinelinking.storage.mongodb.MongoJSONStorageFactory|localhost:7654:jsonpedia:en;" +
            "com.machinelinking.storage.elasticsearch.ElasticJSONStorageFactory|localhost:9300:jsonpedia:en";

    @Override
    protected JSONStorage getJSONStorage() throws UnknownHostException {
        final MultiJSONStorageFactory factory = new MultiJSONStorageFactory();
        final MultiJSONStorageConfiguration config = factory.createConfiguration(CONFIG_URI);
        return factory.createStorage(config);
    }
}
