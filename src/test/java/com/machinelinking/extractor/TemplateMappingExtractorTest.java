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

package com.machinelinking.extractor;

import com.machinelinking.dbpedia.TemplateMapping;
import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateMappingExtractorTest {

    @Test
    public void testFetchMapping() throws IOException, WikiTextParserException, ExecutionException, InterruptedException {
        final TemplateMappingExtractor extractor = new TemplateMappingExtractor();
        final WikiTextParser parser = new WikiTextParser(extractor);
        parser.parse(new URL("http://test/page1"), this.getClass().getResourceAsStream("Page1.wikitext"));

        final Map<String, TemplateMapping> mappings = extractor.getCollectedMappings();
        Assert.assertNotNull(mappings);
        final TemplateMapping infoboxScientist = mappings.get("Infobox scientist");
        Assert.assertNotNull(infoboxScientist);
        Assert.assertTrue(infoboxScientist.getMappingSize() > 20);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer serializer = new JSONSerializer(baos);
        extractor.flushContent(serializer);
        serializer.flush();
        final JsonNode node = JSONUtils.parseJSON(baos.toString());
        Assert.assertTrue( node.get("mapping-collection").get(0).get("mapping").size() > 20 );
    }


}
