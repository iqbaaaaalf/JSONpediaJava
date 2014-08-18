/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.DocumentConverter;
import com.machinelinking.storage.JSONStorage;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.indices.IndexMissingException;


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
    public boolean exists() {
        return exists(null);
    }

    @Override
    public boolean exists(String collection) {
        final String targetCollection = collection == null ? configuration.getCollection() : collection;
        final ElasticJSONStorageConnection connection = openConnection(targetCollection);
        return connection.existsCollection();
    }

    @Override
    public ElasticJSONStorageConnection openConnection() {
        return openConnection(null);
    }

    @Override
    public ElasticJSONStorageConnection openConnection(String collection) {
        final String targetCollection = collection == null ? configuration.getCollection() : collection;
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
                targetCollection,
                converter
        );

    }

    @Override
    public void deleteCollection() {
        deleteCollection(null);
    }

    @Override
    public void deleteCollection(String collection) {
        final String targetCollection = collection == null ? configuration.getCollection() : collection;
        final ElasticJSONStorageConnection connection = openConnection(targetCollection);
        try {
            connection.dropCollection();
        } catch (IndexMissingException ime) {
            // Pass.
        }
    }

    @Override
    public void close() {
        // Empty.
    }

}
