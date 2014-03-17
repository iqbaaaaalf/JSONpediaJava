package com.machinelinking.filter;

/**
 * Factory for {@link com.machinelinking.filter.JSONKeyFilter} and {@link com.machinelinking.filter.JSONObjectFilter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONFilterFactory {

    JSONFilter createEmptyFilter();

    JSONObjectFilter createJSONObjectFilter();

    JSONKeyFilter createJSONJsonKeyFilter();

}
