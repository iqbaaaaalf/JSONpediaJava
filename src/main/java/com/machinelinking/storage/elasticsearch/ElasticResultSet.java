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
