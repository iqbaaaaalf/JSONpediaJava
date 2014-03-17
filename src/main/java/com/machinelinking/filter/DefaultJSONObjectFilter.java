package com.machinelinking.filter;

import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation for {@link JSONFilter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONObjectFilter implements JSONObjectFilter {

    private Map<String,String> criterias = new HashMap<>();

    private JSONFilter nested;

    public void setNested(JSONFilter nested) {
        if(this.nested != null) throw new IllegalStateException("Nested filter already set.");
        this.nested = nested;
    }

    @Override
    public void addCriteria(String fieldName, String fieldPattern) {
        if(criterias.containsKey(fieldName)) throw new IllegalArgumentException();
        criterias.put(fieldName, fieldPattern);
    }

    @Override
    public boolean match(JsonNode node) {
        for(Map.Entry<String,String> criteria : criterias.entrySet()) {
            final JsonNode value = node.get(criteria.getKey());
            if(value == null) return false;
            if(criteria.getValue() == null) continue;
            if(!value.asText().matches(criteria.getValue())) return false;
        }
        return true;
    }

    @Override
    public JSONFilter getNested() {
        return nested;
    }

    @Override
    public boolean isEmpty() {
        return criterias.isEmpty();
    }

    @Override
    public String humanReadable() {
        final StringBuilder sb = new StringBuilder();
        sb.append("object_filter(");
        for(Map.Entry<String,String> criteria : criterias.entrySet()) {
            sb.append(criteria.getKey()).append('=').append(criteria.getValue()).append(',');
        }
        sb.append(')');
        sb.append('>').append(nested == null ? null : nested.humanReadable());
        return sb.toString();
    }

    @Override
    public String toString() {
        return humanReadable();
    }

}
