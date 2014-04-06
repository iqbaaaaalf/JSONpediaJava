package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.Criteria;
import com.machinelinking.storage.Selector;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.machinelinking.storage.Selector} implementation for <i>MongoDB</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoSelector implements Selector {

    private List<Criteria> criterias = new ArrayList<>();
    private List<String> projections = new ArrayList<>();

    @Override
    public void addCriteria(Criteria criteria) {
        criterias.add(criteria);
    }

    @Override
    public void addProjection(String field) {
        projections.add(field);
    }

    Criteria[] getCriterias() {
        return criterias.toArray( new Criteria[criterias.size()] );
    }

    String[] getProjections() {
        return projections.toArray(new String[projections.size()]);
    }

    @Override
    public String toString() {
        return String.format("criterias: %s, projections: %s", criterias.toString(), projections.toString());
    }
}
