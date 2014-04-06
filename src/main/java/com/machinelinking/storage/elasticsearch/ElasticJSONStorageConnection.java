package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.DocumentConverter;
import com.machinelinking.storage.JSONStorageConnection;
import com.machinelinking.storage.JSONStorageConnectionException;
import com.machinelinking.storage.ResultSet;
import com.machinelinking.util.JSONUtils;
import com.machinelinking.wikimedia.WikiPage;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;

import java.io.IOException;

/**
 * {@link com.machinelinking.storage.JSONStorageConnection} implementation for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticJSONStorageConnection implements JSONStorageConnection<ElasticDocument, ElasticSelector> {

    private final Client client;
    private final String db;
    private final String collection;
    private final DocumentConverter<ElasticDocument> converter;

    public ElasticJSONStorageConnection(
            Client client, String db, String collection, DocumentConverter<ElasticDocument> converter
    ) {
        this.client = client;
        this.db = db;
        this.collection = collection;
        this.converter = converter;
    }

    @Override
    public ElasticDocument createDocument(WikiPage page, String json) throws JSONStorageConnectionException {
        try {
            return new ElasticDocument(page.getId(), page.getRevId(), page.getTitle(), JSONUtils.parseJSONAsMap(json));
        } catch (IOException ioe) {
            throw new JSONStorageConnectionException("Error while creating document.", ioe);
        }
    }

    @Override
    public void addDocument(ElasticDocument in) {
        final ElasticDocument document = converter == null ? in : converter.convert(in);
        client
            .prepareIndex(db, collection, Integer.toString(document.getId()))
            .setSource(document.getContent())
            .execute().actionGet();
    }

    @Override
    public void removeDocument(int id) {
        client.prepareDelete(db, collection, Integer.toString(id)).execute().actionGet();
    }

    @Override
    public ElasticDocument getDocument(int id) {
        final GetResponse response = client.prepareGet(db, collection, Integer.toString(id))
            .execute()
            .actionGet();
        return ElasticDocument.unwrap(response.getSource());
    }

    @Override
    public long getDocumentsCount() throws JSONStorageConnectionException {
        try {
            return client.prepareCount(db, collection).execute().get().getCount();
        } catch (Exception e) {
            throw new JSONStorageConnectionException(
                    String.format("Error while counting documents in db: %s collection: %s", db, collection)
            );
        }
    }

    @Override
    public ResultSet<ElasticDocument> query(ElasticSelector selector, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        // Empty.
    }

    @Override
    public void close() {
        client.close();
    }

    protected void dropCollection() {
        final DeleteIndexResponse response = client.admin().indices().prepareDelete(collection).execute().actionGet();
        if(!response.isAcknowledged()) throw new IllegalStateException("Cannot delete index.");
    }

}
