package com.machinelinking.pagestruct;

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
                JSONUtils.parseJSON(
                        "{\n" +
                        "    \"type\": \"mapping\",\n" +
                        "    \"name\": \"TemplateMapping\\n\",\n" +
                        "    \"mapping\": {\n" +
                        "        \"MeltingPt\": \"meltingPoint\",\n" +
                        "        \"melting point C\": \"meltingPoint\",\n" +
                        "        \"boiling point C\": \"boilingPoint\",\n" +
                        "        \"InChI\": \"inchi\",\n" +
                        "        \"Density\": \"density\",\n" +
                        "        \"melting point K\": \"meltingPoint\",\n" +
                        "        \"boiling point K\": \"boilingPoint\",\n" +
                        "        \"PubChem\": \"pubchem\",\n" +
                        "        \"melting point F\": \"meltingPoint\",\n" +
                        "        \"boiling point F\": \"boilingPoint\",\n" +
                        "        \"UNII\": \"fdaUniiCode\",\n" +
                        "        \"Name\": \"foaf:name\",\n" +
                        "        \"ImageFile\": \"foaf:thumbnail\",\n" +
                        "        \"IUPACName\": \"iupacName\",\n" +
                        "        \"BoilingPt\": \"boilingPoint\",\n" +
                        "        \"CASNo\": \"casNumber\"\n" +
                        "    },\n" +
                        "    \"issues\": null\n" +
                        "}"
                ),
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
