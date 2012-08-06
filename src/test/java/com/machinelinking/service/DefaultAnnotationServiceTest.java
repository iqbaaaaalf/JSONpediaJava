package com.machinelinking.service;

import com.machinelinking.WikiEnricherFactory;
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
        Assert.assertEquals(WikiEnricherFactory.Flag.values().length, node.get("definedFlags").size());
    }

    @Test
    public void testAnnotate() throws IOException, URISyntaxException {
        final JsonNode node = performQuery(
                TARGET_RESOURCE
        );
        final JsonNode content = node.get("wikitext-json");
        Assert.assertNotNull(content);
        Assert.assertTrue(content.isArray());
        Assert.assertEquals(1, content.size());
    }

    @Test
    public void testAnnotateOffline() throws IOException, URISyntaxException {
        final JsonNode node = performQuery(
                buildPath(TARGET_RESOURCE).queryParam("flags", WikiEnricherFactory.Flag.Offline).build()
        );
        Assert.assertEquals(1, node.size());
    }

    @Test
    public void testAnnotateWithFilters() throws URISyntaxException, IOException {
        final JsonNode node = performQuery(
                buildPath(TARGET_RESOURCE).queryParam("filter", DefaultJSONFilterEngineTest.FILTER_EXP).build()
        );
        Assert.assertEquals(
                JSONUtils.parseJSON(
                        "{\"filter\":\"__type=template\\nname=Death date and age\\n\"," +
                        "\"result\":[{\"__type\":\"template\",\"name\":\"Death date and age\"," +
                        "\"content\":{\"df\":[\"yes\"],\"1955\":[],\"4\":[],\"18\":[],\"1879\":[],\"3\":[],\"14\":[]}}]}"
                ).toString(),
                node.toString()
        );
    }

    private JsonNode performQuery(String path) throws URISyntaxException, IOException {
        final URI uri = buildPath(path).build();
        return performQuery(uri);
    }

    private UriBuilder buildPath(String path) throws URISyntaxException {
        return  UriBuilder.fromResource(DefaultAnnotationService.class)
                .uri(new URI(String.format("http://%s", HOST))).port(PORT)
                .path(path);
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

}
