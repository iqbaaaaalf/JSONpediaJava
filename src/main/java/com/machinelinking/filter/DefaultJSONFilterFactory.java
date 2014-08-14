package com.machinelinking.filter;

/**
 * Default implementation of {@link com.machinelinking.filter.JSONFilterFactory}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONFilterFactory implements JSONFilterFactory {

    public static final JSONFilter EMPTY_FILTER = new JSONFilter() {
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void setNested(JSONFilter nested) {
            throw new IllegalStateException();
        }

        @Override
        public JSONFilter getNested() {
            return null;
        }

        @Override
        public String humanReadable() {
            return "<empty>";
        }
    };

    @Override
    public JSONFilter createEmptyFilter() {
        return EMPTY_FILTER;
    }

    @Override
    public JSONObjectFilter createJSONObjectFilter() {
        return new DefaultJSONObjectFilter();
    }

    @Override
    public JSONKeyFilter createJSONJsonKeyFilter() {
        return new DefaultJSONKeyFilter();
    }

}
