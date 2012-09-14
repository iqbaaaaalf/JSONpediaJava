package com.machinelinking.filter;

import org.codehaus.jackson.JsonNode;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONFilter {

    void addCriteria(String fieldName, String fieldValue);

    boolean match(JsonNode node);

    boolean isEmpty();

    String print();

}
