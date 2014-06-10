package com.machinelinking.storage;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MultiSelector implements Selector {

    MultiSelector() {}

    @Override
    public void addCriteria(Criteria criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addProjection(String field) {
        throw new UnsupportedOperationException();
    }

}
