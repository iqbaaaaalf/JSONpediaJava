package com.machinelinking.dbpedia;

import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import junit.framework.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Test case for {@link TemplateMappingHandler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateMappingHandlerTest {

    // http://mappings.dbpedia.org/index.php?title=Mapping:Infobox_scientist&action=edit

    @Test
    public void testMappingReading() throws IOException, WikiTextParserException {
        final TemplateMapping[] out = new TemplateMapping[1];
        final TemplateMappingHandler handler = new TemplateMappingHandler("Mapping1") {
            @Override
            public void handle(TemplateMapping mapping) {
                out[0] = mapping;
            }
        };
        final WikiTextParser parser = new WikiTextParser(handler);
        parser.parse(
                new URL("http://test/url"),
                new BufferedReader(new InputStreamReader(
                        this.getClass().getResourceAsStream(
                            "/com/machinelinking/dbpedia/Mapping1.wikitext"
                        )
                ))
        );
        final TemplateMapping templateMapping = out[0];
        Assert.assertEquals("Mapping1", templateMapping.getMappingName());
        Assert.assertEquals("Congressman", templateMapping.getMappingClass());
        Assert.assertEquals(396, templateMapping.getMappingSize());
    }

}
