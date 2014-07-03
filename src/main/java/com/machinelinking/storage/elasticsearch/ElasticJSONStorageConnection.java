package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.Criteria;
import com.machinelinking.storage.DocumentConverter;
import com.machinelinking.storage.JSONStorageConnection;
import com.machinelinking.storage.JSONStorageConnectionException;
import com.machinelinking.util.JSONUtils;
import com.machinelinking.wikimedia.WikiPage;
import org.codehaus.jackson.util.TokenBuffer;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

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

    protected ElasticJSONStorageConnection(
            Client client, String db, String collection, DocumentConverter<ElasticDocument> converter
    ) {
        this.client = client;
        this.db = db;
        this.collection = collection;
        this.converter = converter;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public ElasticDocument createDocument(WikiPage page, TokenBuffer buffer) throws JSONStorageConnectionException {
        try {
            return new ElasticDocument(page.getId(), page.getRevId(), page.getTitle(), JSONUtils.parseJSONAsMap(buffer));
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
        final String index = getIndex(db, collection);
        try {
            return client.prepareCount(index).execute().get().getCount();
        } catch (Exception e) {
            throw new JSONStorageConnectionException(
                    String.format("Error while counting documents in index [%s]", index)
            );
        }
    }

    @Override
    public ElasticResultSet query(ElasticSelector selector, int limit) throws JSONStorageConnectionException {
        final String index = getIndex(db, collection);
        try {
            SearchRequestBuilder requestBuilder = client
                    .prepareSearch(index)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            final QueryBuilder queryBuilder;
            if (selector.getCriterias().isEmpty()) {
                queryBuilder = QueryBuilders.matchAllQuery();
            } else {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                for (Criteria criteria : selector.getCriterias()) {
                    if (criteria.operator != Criteria.Operator.eq)
                        throw new IllegalArgumentException("Unsupported operators different from [eq]");
                    boolQueryBuilder.must(
                            QueryBuilders.termQuery(criteria.field == null ? null : criteria.field, criteria.value));
                }
                queryBuilder = boolQueryBuilder;
            }
            requestBuilder.setQuery(queryBuilder);
            requestBuilder.setFrom(0).setSize(limit).setExplain(false);
            final SearchResponse response = requestBuilder.execute().actionGet();
            return new ElasticResultSet(requestBuilder.toString(), response);
        } catch (Exception e) {
            throw new JSONStorageConnectionException("Error while waiting for response.", e);
        }
    }

    @Override
    public void flush() {
        // Empty.
    }

    @Override
    public void close() {
        client.close();
    }

    protected boolean existsCollection() {
        final String index = getIndex(db, collection);
        return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
    }

    protected void dropCollection() {
        final String index = getIndex(db, collection);
        final DeleteIndexResponse response = client.admin().indices().prepareDelete(index).execute().actionGet();
        if(!response.isAcknowledged())
            throw new IllegalStateException(String.format("Cannot delete index [%s]", index));
    }

    private String getIndex(String db, String collection) {
        //return String.format("%s-%s", db, collection);
        return db;
    }

}
