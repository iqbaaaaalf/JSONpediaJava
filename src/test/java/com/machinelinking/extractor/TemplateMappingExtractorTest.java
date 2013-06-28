package com.machinelinking.extractor;

import com.machinelinking.dbpedia.TemplateMapping;
import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

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
        System.out.println(baos);
        final JsonNode node = JSONUtils.parseJSON(baos.toString());
        Assert.assertTrue( node.get("mapping-collection").get(0).get("mapping").size() > 20 );
    }


}
