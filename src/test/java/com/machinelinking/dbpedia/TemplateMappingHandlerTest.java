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

package com.machinelinking.dbpedia;

import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import org.testng.Assert;
import org.testng.annotations.Test;

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
        Assert.assertEquals(templateMapping.getMappingName(), "Mapping1");
        Assert.assertEquals(templateMapping.getMappingClass(), "Congressman");
        Assert.assertEquals(templateMapping.getMappingSize(), 396);
    }

}
