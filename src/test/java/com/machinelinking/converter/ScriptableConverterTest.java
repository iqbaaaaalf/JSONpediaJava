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

import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.util.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Test case for {@link com.machinelinking.converter.ScriptableConverter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ScriptableConverterTest {

    @Test
    public void testConvert() throws IOException, ConverterException, ScriptableFactoryException {
        final String script = IOUtils.toString(this.getClass().getResourceAsStream("scriptable-converter-test1.py"));
        final ScriptableConverter converter = ScriptableConverterFactory.getInstance().createConverter(script);
        final ByteArrayOutputStream serializerBAOS = new ByteArrayOutputStream();
        final JSONSerializer serializer = new JSONSerializer(serializerBAOS);
        final ByteArrayOutputStream writerBAOS = new ByteArrayOutputStream();
        final Writer writer = new BufferedWriter(new OutputStreamWriter(writerBAOS));
        converter.convertData(
            JSONUtils.parseJSONAsMap(
                "{\"@type\":\"reference\",\"label\":\"List of Nobel laureates in Physics\"," +
                 "\"content\":{\"@an0\":\"1921\"}}"
            ),
            serializer,
            writer
        );
        serializer.close();
        writer.close();
        Assert.assertEquals(
                serializerBAOS.toString(),
                "{\"link\":\"List of Nobel laureates in Physics 1921\"}"
                );
        Assert.assertEquals(
                writerBAOS.toString(),
                "<a href=\"List of Nobel laureates in Physics\">1921</a>"
        );
    }

}
