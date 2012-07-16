package com.machinelinking.service;

import com.machinelinking.WikiEnricherFactory;
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
            TARGET_RESOURCE = "resource/" + URLEncoder.encode("http://en.wikipedia.org/wiki/Albert_Einstein", "UTF8");
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
                "resource/" + URLEncoder.encode("http://en.wikipedia.org/wiki/Albert_Einstein", "UTF8")
        );
        final JsonNode content = node.get("wikitext-json");
        Assert.assertNotNull(content);
        Assert.assertTrue(content.isArray());
        Assert.assertEquals(1, content.size());
    }

    // http://localhost:9998/annotate/resource/http%3A%2F%2Fen.wikipedia.org%2Fpage%2FAlbert_Einstein?flags=Offline
    @Test
    public void testAnnotateOffline() throws IOException, URISyntaxException {
        final JsonNode node = performQuery(
                buildPath(TARGET_RESOURCE).queryParam("flags", WikiEnricherFactory.Flag.Offline).build()
        );
        Assert.assertEquals(1, node.size());
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
        System.out.println("CONTENT: " + content.toString());
        return JSONUtils.parseJSON(content.toString());
    }

}
