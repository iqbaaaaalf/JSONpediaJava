package com.machinelinking.filter;

import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation for {@link JSONFilter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONFilter implements JSONFilter {

    private Map<String,String> criterias = new HashMap<>();

    @Override
    public void addCriteria(String fieldName, String fieldPattern) {
        if(criterias.containsKey(fieldName)) throw new IllegalArgumentException();
        criterias.put(fieldName, fieldPattern);
    }

    @Override
    public boolean match(JsonNode node) {
        for(Map.Entry<String,String> criteria : criterias.entrySet()) {
            final JsonNode value = node.get(criteria.getKey());
            if(value == null || ! value.asText().matches(criteria.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return criterias.isEmpty();
    }

    @Override
    public String print() {
        final StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> criteria : criterias.entrySet()) {
            sb.append(criteria.getKey()).append('=').append(criteria.getValue()).append('\n');
        }
        return sb.toString();
    }

}
