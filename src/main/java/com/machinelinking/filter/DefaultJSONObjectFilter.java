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

    private final Map<String,String> criterias = new HashMap<>();

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
    public String getCriteriaPattern(String fieldName) {
        return criterias.get(fieldName);
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
    public int hashCode() {
        return 2 * criterias.hashCode() * 3 * (nested == null ? 1 : nested.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(!(obj instanceof DefaultJSONObjectFilter)) return false;
        final DefaultJSONObjectFilter other = (DefaultJSONObjectFilter) obj;
        return
            this.criterias.equals(other.criterias)
                &&
            (this.nested == null ? other.nested == null : this.nested.equals(other.nested));
    }

    @Override
    public String toString() {
        return humanReadable();
    }

}
