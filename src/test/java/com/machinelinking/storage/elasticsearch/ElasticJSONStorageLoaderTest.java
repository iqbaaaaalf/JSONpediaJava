package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.AbstractJSONStorageLoaderTest;
import com.machinelinking.storage.JSONStorage;

import java.net.UnknownHostException;

/**
 * {@link com.machinelinking.storage.elasticsearch.ElasticJSONStorage} test.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticJSONStorageLoaderTest extends AbstractJSONStorageLoaderTest {

    public static final String TEST_STORAGE_DB = "jsonpedia_test_load";
    public static final String TEST_STORAGE_COLLECTION = "en";

    @Override
    protected JSONStorage getJSONStorage() throws UnknownHostException {
        return new ElasticJSONStorage(
                new ElasticJSONStorageConfiguration("localhost", 9300, TEST_STORAGE_DB, TEST_STORAGE_COLLECTION),
                new ElasticDocumentConverter()
        );
    }

}
