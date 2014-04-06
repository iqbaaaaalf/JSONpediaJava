package com.machinelinking.storage;

/**
 * Defines a storage query selector.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Selector {

    /**
     * Adds a selection criteria.
     *
     * @param criteria
     */
    void addCriteria(Criteria criteria);

    /**
     * Adds a projection field.
     *
     * @param field
     */
    void addProjection(String field);

}
