package com.machinelinking.enricher;

import com.machinelinking.parser.DocumentSource;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiEnricherTest {

    @Test
    public void testEnrich()
    throws IOException, SAXException, WikiTextParserException, InterruptedException, ExecutionException {
        final WikiEnricher enricher = WikiEnricherFactory
                .getInstance()
                .createFullyConfiguredInstance(
                        WikiEnricherFactory.Online,
                        WikiEnricherFactory.Validate,
                        WikiEnricherFactory.Extractors,
                        WikiEnricherFactory.Splitters,
                        WikiEnricherFactory.Structure
                );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer serializer = new JSONSerializer(baos);
        enricher.enrichEntity(
                new DocumentSource(
                    new URL("http://en.wikipedia.org/wiki/Albert_Einstein"),
                    this.getClass().getResourceAsStream("/Page1.wikitext")
                ),
                serializer
        );
        System.out.println("OUT " + baos);
        final JsonNode expectedJSON = JSONUtils.parseJSON(
                this.getClass().getResourceAsStream("/Enrichment.json")
        );
        final JsonNode extractedJSON = JSONUtils.parseJSON(baos.toString());
        resetConfidence(expectedJSON);
        resetConfidence(extractedJSON);
        Assert.assertTrue( expectedJSON.equals(extractedJSON) );
    }

    private void resetConfidence(JsonNode node) {
        final ObjectNode onode = (ObjectNode) node.get("freebase");
        onode.put("relevance:score", 0);
    }

}
