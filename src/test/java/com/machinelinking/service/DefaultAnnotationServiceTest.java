package com.machinelinking.service;

import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.filter.DefaultJSONFilterEngineTest;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultAnnotationServiceTest extends ServiceTestBase {

    private static final String[] EXPECTED_ARRAY_NODES = {
        "sections", "links", "references"
    };

    private static final String[] EXPECTED_OBJECT_NODES = {
        "templates", "categories"
    };

    private static final String TARGET_RESOURCE;

    static {
        try {
            TARGET_RESOURCE = "resource/json/" + URLEncoder.encode("http://en.wikipedia.org/wiki/Albert_Einstein", "UTF8");
        } catch (UnsupportedEncodingException urise) {
            throw new IllegalStateException();
        }
    }

    @Test
    public void testFlags() throws IOException, URISyntaxException {
        final JsonNode node = performQuery("flags");
        Assert.assertEquals(WikiEnricherFactory.getInstance().getDefinedFlags().length, node.get("definedFlags").size());
    }

    @Test
    public void testAnnotate() throws IOException, URISyntaxException {
        checkJSONResponse( performQuery(TARGET_RESOURCE) );
    }

    @Test
    public void testAnnotateOnline() throws IOException, URISyntaxException {
        final JsonNode node = performQuery(
                buildPath(TARGET_RESOURCE).queryParam("flags", WikiEnricherFactory.Linkers).build()
        );
        checkJSONResponse(node);
        Assert.assertNotNull(node.get("freebase"));
    }

    @Test
    public void testAnnotateWithFilters() throws URISyntaxException, IOException {
        final JsonNode node = performQuery(
                buildPath(TARGET_RESOURCE)
                        .queryParam("flags", WikiEnricherFactory.Structure)
                        .queryParam("filter", DefaultJSONFilterEngineTest.FILTER_EXP).build()
        );
        Assert.assertEquals(
                JSONUtils.parseJSON(
                        "{\"filter\":\"__type=template\\nname=Death date and age\\n\",\"result\":" +
                        "[{\"__type\":\"template\",\"name\":\"Death date and age\",\"content\":" +
                         "{\"df\":[\"yes\"],\"__anon_6\":[\"1955\"],\"__anon_7\":[\"4\"],\"__anon_8\":[\"18\"]," +
                          "\"__anon_9\":[\"1879\"],\"__anon_10\":[\"3\"],\"__anon_11\":[\"14\"]}}]}"
                ).toString(),
                node.toString()
        );
    }

    private UriBuilder buildPath(String path) throws URISyntaxException {
        return  UriBuilder.fromResource(DefaultAnnotationService.class)
                .uri(new URI(String.format("http://%s", BasicServer.DEFAULT_HOST))).port(BasicServer.DEFAULT_PORT)
                .path(path);
    }

    private JsonNode performQuery(String path) throws URISyntaxException, IOException {
        final URI uri = buildPath(path).build();
        return performQuery(uri);
    }

    private JsonNode performQuery(URI uri) throws IOException {
        final InputStream is = uri.toURL().openStream();
        final BufferedReader br = new BufferedReader( new InputStreamReader(is) );
        final StringBuilder content = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            content.append(line);
        }
        return JSONUtils.parseJSON(content.toString());
    }

    private void checkJSONResponse(JsonNode node) {
        for (String expectedNode : EXPECTED_ARRAY_NODES) {
            final JsonNode content = node.get(expectedNode);
            Assert.assertNotNull("Cannot find object node: " + expectedNode, content);
            Assert.assertTrue("Invalid content for " + expectedNode, content.isArray());
        }
        for (String expectedNode : EXPECTED_OBJECT_NODES) {
            final JsonNode content = node.get(expectedNode);
            Assert.assertNotNull("Cannot find object node: " + expectedNode, content);
            Assert.assertTrue("Invalid content for " + expectedNode, content.isObject());
        }
    }

}
