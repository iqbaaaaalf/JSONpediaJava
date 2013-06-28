package com.machinelinking.dbpedia;

import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateMappingTest {

    @Test
    public void testToJSON() throws IOException, WikiTextParserException, SAXException {
        final TemplateMapping mapping = TemplateMapping.readMappingForTemplate("Chembox");
        final String json = JSONUtils.serializeToJSON(mapping);
        System.out.println( json );
        Assert.assertEquals(
                JSONUtils.parseJSON( this.getClass().getResourceAsStream("Mapping1.json") ),
                JSONUtils.parseJSON(json)
        );
    }

    @Test
    public void testReadMappingForTemplate() throws IOException, WikiTextParserException, SAXException {
        final TemplateMapping mapping = TemplateMapping.readMappingForTemplate("Infobox scientist");
        System.out.println( JSONUtils.serializeToJSON(mapping) );
        Assert.assertEquals( 23, mapping.getMappingSize() );
    }

}
