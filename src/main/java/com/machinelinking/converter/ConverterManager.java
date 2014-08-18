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

package com.machinelinking.converter;

import com.machinelinking.filter.JSONObjectFilter;
import com.machinelinking.serializer.Serializer;
import org.codehaus.jackson.JsonNode;

import java.io.Writer;

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
    boolean removeConverter(JSONObjectFilter filter);

    /**
     * Retrieves the converter matching the given data.
     *
     * @param data
     * @return
     */
    Converter getConverterForData(JsonNode data);

    /**
     * Applies recursively {@link #getConverterForData(org.codehaus.jackson.JsonNode)} on every subnode.
     *
     * @param data
     */
    void process(JsonNode data, Serializer serializer, Writer writer) throws ConverterException;

}
