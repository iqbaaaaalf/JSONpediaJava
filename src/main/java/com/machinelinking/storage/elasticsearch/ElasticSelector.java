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

import com.machinelinking.storage.Criteria;
import com.machinelinking.storage.Selector;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.machinelinking.storage.Selector} implementation for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticSelector implements Selector {

    private final List<Criteria> criterias = new ArrayList<>();

    @Override
    public void addCriteria(Criteria criteria) {
        criterias.add(criteria);
    }

    @Override
    public void addProjection(String field) {
        throw new UnsupportedOperationException();
    }

    //TODO: add full support for query string?
    // http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html#query-string-syntax
    public QueryBuilder buildQuery() {
        final QueryBuilder queryBuilder;
        if (criterias.isEmpty()) {
            queryBuilder = QueryBuilders.matchAllQuery();
        } else {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (Criteria criteria : criterias) {
                if (criteria.operator != Criteria.Operator.eq)
                    throw new IllegalArgumentException("Unsupported operators different from [eq]");
                boolQueryBuilder.must(
                        QueryBuilders.matchQuery(criteria.field == null ? "_all" : criteria.field, criteria.value)
                );
            }
            queryBuilder = boolQueryBuilder;
        }
        return queryBuilder;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        for(Criteria criteria: criterias) {
            sb.append(criteria);
            if(i < criterias.size() - 2) sb.append(';');
            i++;
        }
        return sb.toString();
    }
}
