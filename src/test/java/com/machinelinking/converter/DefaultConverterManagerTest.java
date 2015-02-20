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

import com.machinelinking.filter.DefaultJSONFilterEngine;
import com.machinelinking.filter.DefaultJSONFilterFactory;
import com.machinelinking.filter.DefaultJSONFilterParser;
import com.machinelinking.filter.JSONFilterFactory;
import com.machinelinking.filter.JSONFilterParser;
import com.machinelinking.filter.JSONObjectFilter;
import com.machinelinking.main.JSONpedia;
import com.machinelinking.main.JSONpediaException;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Test case for {@link com.machinelinking.converter.DefaultConverterManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultConverterManagerTest {

    @Test
    public void testConverterAddRemove() throws ScriptableFactoryException, IOException {
        final JSONFilterFactory filterFactory = new DefaultJSONFilterFactory();
        final JSONFilterParser filterParser = new DefaultJSONFilterParser();
        final JSONObjectFilter f1 = (JSONObjectFilter) filterParser.parse("@type:link,name:x", filterFactory);

        final ConverterManager converterManager = new DefaultConverterManager();
        final Converter converter = new Converter() {
            @Override
            public void convertData(Map<String, ?> data, Serializer serializer, Writer writer)
            throws ConverterException {
                // Empty.
            }
        };
        Assert.assertTrue(converterManager.addConverter(f1, converter));
        Assert.assertFalse(converterManager.addConverter(f1, converter));
        Assert.assertEquals(converter,
            converterManager.getConverterForData(
                JSONUtils.parseJSON("{\"@type\" : \"link\", \"name\" : \"x\", \"content\" : {} }")
            )
        );
    }

    @Test
    public void testProcess() throws IOException, JSONpediaException, ConverterException {
        Converter converter = new Converter() {
            @Override
            public void convertData(Map<String, ?> data, Serializer serializer, Writer writer)
                throws ConverterException {
                try {
                    final String value = data.get("label").toString().toUpperCase();
                    writer.append(value).append(", ");
                    serializer.value(value);
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        };

        final ConverterManager converterManager = new DefaultConverterManager();
        converterManager.addConverter(
                (JSONObjectFilter) DefaultJSONFilterEngine.parseFilter("@type:reference"),
                converter
        );

        ByteArrayOutputStream serializerBuffer = new ByteArrayOutputStream();
        JSONSerializer serializer = new JSONSerializer(serializerBuffer);
        ByteArrayOutputStream writerBuffer = new ByteArrayOutputStream();
        Writer writer = new PrintWriter(writerBuffer);
        JsonNode data = JSONpedia.instance().process("en:London").flags("Structure").json();

        serializer.openList();
        converterManager.process(data, serializer, writer);
        serializer.closeList();
        serializer.close();
        writer.close();

        final String writerContent = writerBuffer.toString();
        Assert.assertTrue(writerContent.contains("CITY"));
        Assert.assertTrue(writerContent.contains("LONDON"));
        Assert.assertTrue(writerContent.contains("BUCKINGHAM PALACE"));
        Assert.assertTrue(writerContent.contains("WESTMINSTER"));
        Assert.assertTrue(writerContent.length() > 1000);
        final JsonNode serializerContent = JSONUtils.parseJSON( serializerBuffer.toString() );
        Assert.assertTrue( serializerContent.size() > 1100);
    }

}
