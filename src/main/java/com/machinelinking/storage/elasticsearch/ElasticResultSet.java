package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.ResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.io.Closeable;
import java.util.Iterator;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticResultSet implements ResultSet<ElasticDocument>, Closeable {

    private final String explain;
    private final SearchResponse response;
    private final Iterator<SearchHit> hits;

    public ElasticResultSet(String explain, SearchResponse response) {
        this.explain = explain;
        this.response = response;
        this.hits= response.getHits().iterator();
    }

    public String getExplain() {
        return explain;
    }

    @Override
    public long getCount() {
        return response.getHits().getTotalHits();
    }

    @Override
    public ElasticDocument next() {
        if(hits.hasNext()) {
            return ElasticDocument.unwrap(hits.next().getSource());
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        // Empty.
    }

}
