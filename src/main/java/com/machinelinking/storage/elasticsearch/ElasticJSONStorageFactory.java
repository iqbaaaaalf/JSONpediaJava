package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.AbstractJSONStorageFactory;
import com.machinelinking.storage.DocumentConverter;
import com.machinelinking.storage.JSONStorageConfiguration;

/**
 * {@link com.machinelinking.storage.JSONStorageFactory} implementation for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticJSONStorageFactory extends AbstractJSONStorageFactory {

    @Override
    public ElasticJSONStorageConfiguration createConfiguration(String configURI) {
        final Configuration c = parseConfigurationURI(configURI);
        return new ElasticJSONStorageConfiguration(c.host, c.port, c.db, c.collection);
    }

    @Override
    public ElasticJSONStorage createStorage(JSONStorageConfiguration config, DocumentConverter converter) {
        if(!(config instanceof JSONStorageConfiguration)) throw new IllegalArgumentException();
        return new ElasticJSONStorage((ElasticJSONStorageConfiguration)config, converter);
    }

    @Override
    public ElasticJSONStorage createStorage(JSONStorageConfiguration config) {
        return createStorage(config, null);
    }

}
