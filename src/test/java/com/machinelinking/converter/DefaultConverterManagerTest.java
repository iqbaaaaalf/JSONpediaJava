package com.machinelinking.converter;

import com.machinelinking.filter.DefaultJSONFilterFactory;
import com.machinelinking.filter.DefaultJSONFilterParser;
import com.machinelinking.filter.JSONFilterFactory;
import com.machinelinking.filter.JSONFilterParser;
import com.machinelinking.filter.JSONObjectFilter;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.util.JSONUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
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
        final JSONObjectFilter f1 = (JSONObjectFilter) filterParser.parse("__type:link,name:x", filterFactory);

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
                JSONUtils.parseJSON("{\"__type\" : \"link\", \"name\" : \"x\", \"content\" : {} }")
            )
        );
    }

}
