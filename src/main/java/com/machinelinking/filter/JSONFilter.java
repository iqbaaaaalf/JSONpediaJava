package com.machinelinking.filter;

import org.codehaus.jackson.JsonNode;

/**
 * Defines a filter applicable over a <i>JSON</i> object.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONFilter {

    /**
     * Adds a filtering criteria based on exact matching.
     *
     * @param fieldName name of the field to match.
     * @param fieldValue value of the field to match.
     */
    void addCriteria(String fieldName, String fieldValue);

    /**
     * @param node
     * @return <code>true</code> if match is satisfied, <code>false</code> otherwise.
     */
    boolean match(JsonNode node);

    /**
     * @return <code>true</code> if filter is empty, <code>false</code> otherwise.
     */
    boolean isEmpty();

    String print();

}
