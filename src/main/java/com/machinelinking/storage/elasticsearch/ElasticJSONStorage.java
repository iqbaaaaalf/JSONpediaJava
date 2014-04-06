package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.DocumentConverter;
import com.machinelinking.storage.JSONStorage;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;


/**
 * {@link com.machinelinking.storage.JSONStorage} implementation for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticJSONStorage
    implements JSONStorage<ElasticJSONStorageConfiguration, ElasticDocument, ElasticSelector> {

    private final ElasticJSONStorageConfiguration configuration;

    private final DocumentConverter<ElasticDocument> converter;

    public ElasticJSONStorage(
            ElasticJSONStorageConfiguration configuration,
            DocumentConverter<ElasticDocument> converter
    ) {
        this.configuration = configuration;
        this.converter = converter;
    }

    @Override
    public ElasticJSONStorageConfiguration getConfiguration() {

        return configuration;
    }

    @Override
    public DocumentConverter<ElasticDocument> getConverter() {
        return converter;
    }

    @Override
    public ElasticJSONStorageConnection openConnection(String table) {
        final TransportClient client = new TransportClient();
        client.addTransportAddress(
                new InetSocketTransportAddress(
                        getConfiguration().getHost(),
                        getConfiguration().getPort()
                )
        );
        return new ElasticJSONStorageConnection(
                client,
                getConfiguration().getDB(),
                getConfiguration().getCollection(),
                converter
        );

    }

    @Override
    public void deleteCollection(String collection) {
        final ElasticJSONStorageConnection connection = openConnection(collection);
        connection.dropCollection();
    }

    @Override
    public void close() {
        // Empty.
    }

}
