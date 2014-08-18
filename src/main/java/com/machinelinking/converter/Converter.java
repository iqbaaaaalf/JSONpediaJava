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
