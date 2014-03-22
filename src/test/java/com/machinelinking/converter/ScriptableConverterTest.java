package com.machinelinking.converter;

import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.util.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

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
                "{\"__type\":\"reference\",\"label\":\"List of Nobel laureates in Physics\"," +
                 "\"content\":{\"__an0\":\"1921\"}}"
            ),
            serializer,
            writer
        );
        serializer.close();
        writer.close();
        Assert.assertEquals(
                "{\"link\":\"List of Nobel laureates in Physics 1921\"}",
                serializerBAOS.toString()
        );
        Assert.assertEquals(
                "<a href=\"List of Nobel laureates in Physics\">1921</a>",
                writerBAOS.toString()
        );
    }

}
