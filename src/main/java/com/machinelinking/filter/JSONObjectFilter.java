package com.machinelinking.filter;

import org.codehaus.jackson.JsonNode;

/**
 * Defines a filter of an object satisfying a set of criteria.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONObjectFilter extends JSONFilter {

    /**
     * Adds a filtering criteria based on exact matching.
     *
     * @param fieldName name of the field to match.
     * @param fieldPattern the pattern to match for the field value.
     */
    void addCriteria(String fieldName, String fieldPattern);

    /**
     * @param node
     * @return <code>true</code> if match is satisfied, <code>false</code> otherwise.
     */
    boolean match(JsonNode node);

}
