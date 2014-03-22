package com.machinelinking.converter;

import com.machinelinking.serializer.Serializer;

import java.io.Writer;
import java.util.Map;

/**
 * Defines a generic data converter.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Converter {

    /**
     * Takes a <code>data</code> as input and provides a JSON and text serialization.
     *
     * @param data
     * @param serializer
     * @param writer
     * @throws ConverterException
     */
    void convertData(Map<String,?> data, Serializer serializer, Writer writer) throws ConverterException;

}
