package com.machinelinking.dbpedia;

import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Test case for {@link TemplateMapping} lookup.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateMappingTest {

    private static final Logger logger = Logger.getLogger(TemplateMappingTest.class);

    @Test
    public void testToJSON() throws IOException, WikiTextParserException, SAXException {
        final TemplateMapping mapping = TemplateMapping.readMappingForTemplate("Chembox");
        final String json = JSONUtils.serializeToJSON(mapping);
        logger.debug(json);
        Assert.assertEquals(
                JSONUtils.parseJSON( this.getClass().getResourceAsStream("Mapping1.json") ),
                JSONUtils.parseJSON(json)
        );
    }

    @Test
    public void testReadMappingForTemplate() throws IOException, WikiTextParserException, SAXException {
        final TemplateMapping mapping = TemplateMapping.readMappingForTemplate("Infobox scientist");
        logger.debug(JSONUtils.serializeToJSON(mapping));
        Assert.assertEquals( 23, mapping.getMappingSize() );
    }

}
