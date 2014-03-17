package com.machinelinking.filter;

/**
 * Default {@link com.machinelinking.filter.JSONKeyFilter} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONKeyFilter implements JSONKeyFilter {

    private String criteria;
    private JSONFilter nested;

    public DefaultJSONKeyFilter() {}

    public void setCriteria(String c) {
        if(this.criteria != null) throw new IllegalStateException();
        this.criteria = c;
    }

    public void setNested(JSONFilter n) {
        if(this.nested != null) throw new IllegalStateException();
        this.nested = n;
    }

    @Override
    public boolean matchKey(String key) {
        return criteria.matches(key);
    }

    @Override
    public JSONFilter getNested() {
        return nested;
    }

    @Override
    public boolean isEmpty() {
        return criteria == null;
    }

    @Override
    public String humanReadable() {
        return String.format("key_filter(%s)>%s", criteria, nested == null ? null : nested.humanReadable());
    }

    @Override
    public String toString() {
        return humanReadable();
    }
}
