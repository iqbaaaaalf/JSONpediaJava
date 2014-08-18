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

package com.machinelinking.enricher;

import com.machinelinking.parser.DocumentSource;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Test case for {@link WikiEnricher}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiEnricherTest {

    private static final Logger logger = Logger.getLogger(WikiEnricherTest.class);

    @Test
    public void testEnrich1()
    throws IOException, SAXException, WikiTextParserException, InterruptedException, ExecutionException {
        verifyEnrich(
                new URL("http://en.wikipedia.org/wiki/Albert_Einstein"), true, "Page1.wikitext", "Page1.json"
        );
    }

    @Test
    public void testEnrich2()
    throws IOException, SAXException, WikiTextParserException, InterruptedException, ExecutionException {
        verifyEnrich(
                new URL("http://en.wikipedia.org/wiki/London"), false, "Page2.wikitext", "Page2.json"
        );
    }

    private void verifyEnrich(URL entity, boolean online, String wikiInResource, String jsonOutExpectedResource)
    throws IOException, WikiTextParserException, SAXException, ExecutionException, InterruptedException {
        final List<Flag> flags = new ArrayList<>();
        if(online) flags.add(WikiEnricherFactory.Linkers);
        flags.add(WikiEnricherFactory.Validate);
        flags.add(WikiEnricherFactory.Extractors);
        flags.add(WikiEnricherFactory.Splitters);
        flags.add(WikiEnricherFactory.Structure);

        final InputStream inInputStream = this.getClass().getResourceAsStream(wikiInResource);
        if(inInputStream == null) throw new NullPointerException("Cannot find input resource");
        final InputStream expectedInStream = this.getClass().getResourceAsStream(jsonOutExpectedResource);
        if(expectedInStream == null) throw new NullPointerException("Cannot find expected resource.");

        final WikiEnricher enricher = WikiEnricherFactory
                .getInstance().createFullyConfiguredInstance( flags.toArray( new Flag[flags.size()] ) );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer serializer = new JSONSerializer(baos);
        enricher.enrichEntity(
                new DocumentSource(entity, inInputStream),
                serializer
        );
        logger.debug("JSON Output: " + baos);

        final JsonNode expectedJSON = JSONUtils.parseJSON(expectedInStream);
        final JsonNode extractedJSON = JSONUtils.parseJSON(baos.toString());
        removeVariableData(expectedJSON);
        removeVariableData(extractedJSON);
        Assert.assertTrue( expectedJSON.equals(extractedJSON) );
    }

    private void removeVariableData(JsonNode node) {
        final ObjectNode onode = (ObjectNode) node.get("freebase");
        if(onode != null) onode.put("score", 0);
        ((ObjectNode) node).put("abstract", (String) null);
    }

}
