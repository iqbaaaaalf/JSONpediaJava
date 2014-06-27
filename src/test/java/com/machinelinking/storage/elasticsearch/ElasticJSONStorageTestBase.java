package com.machinelinking.storage.elasticsearch;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class ElasticJSONStorageTestBase {

    public static String TEST_DB = "jsonpedia_test_db";
    public static String TEST_COLLECTION = "test_collection";

    public ElasticJSONStorage createStorage(String db, String collection) {
        final ElasticJSONStorageFactory factory = new ElasticJSONStorageFactory();
        return factory.createStorage(
                factory.createConfiguration(
                        String.format("localhost:9300:%s:%s", db, collection)
                )
        );
    }

    public ElasticJSONStorage createStorage() {
        return createStorage(TEST_DB, TEST_COLLECTION);
    }

}
