package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.AbstractJSONStorageFactory;
import com.machinelinking.storage.DocumentConverter;

/**
 * {@link com.machinelinking.storage.JSONStorageFactory} implementation for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticJSONStorageFactory extends AbstractJSONStorageFactory<ElasticJSONStorageConfiguration, ElasticJSONStorage, ElasticDocument> {

    @Override
    public ElasticJSONStorageConfiguration createConfiguration(String configURI) {
        final Configuration c = parseConfigurationURI(configURI);
        return new ElasticJSONStorageConfiguration(c.host, c.port, c.db, c.collection);
    }

    @Override
    public ElasticJSONStorage createStorage(ElasticJSONStorageConfiguration config, DocumentConverter<ElasticDocument> converter) {
        return new ElasticJSONStorage(config, converter);
    }

    @Override
    public ElasticJSONStorage createStorage(ElasticJSONStorageConfiguration config) {
        return createStorage(config, new ElasticDocumentConverter());
    }

}
