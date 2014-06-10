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

    @Override
    protected JSONStorage getJSONStorage() throws UnknownHostException {
        return new ElasticJSONStorage(
                new ElasticJSONStorageConfiguration("localhost", 9300, "jsonpedia-test", "en"),
                new ElasticDocumentConverter()
        );
    }

}
