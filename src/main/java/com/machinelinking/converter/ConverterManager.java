package com.machinelinking.converter;

import com.machinelinking.filter.JSONObjectFilter;
import org.codehaus.jackson.JsonNode;

/**
 * Allows to register a set of converters for specific <b>JSON</b> data matching the given
 * {@link com.machinelinking.filter.JSONObjectFilter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface ConverterManager {

    /**
     * Adds a converter.
     *
     * @param filter
     * @param converter
     * @return
     */
    boolean addConverter(JSONObjectFilter filter, Converter converter);

    /**
     * Removes a converter.
     *
     * @param filter
     * @return
     */
    boolean  removeConverter(JSONObjectFilter filter);

    /**
     * Retrieves the converter matching the given data.
     *
     * @param data
     * @return
     */
    Converter getConverterForData(JsonNode data);

}
