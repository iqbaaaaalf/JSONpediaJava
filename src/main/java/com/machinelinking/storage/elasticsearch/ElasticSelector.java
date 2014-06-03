package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.Criteria;
import com.machinelinking.storage.Selector;

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

    List<Criteria> getCriterias() {
        return criterias;
    }

}
