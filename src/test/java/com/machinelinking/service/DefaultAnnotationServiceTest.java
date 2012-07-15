package com.machinelinking.service;

import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultAnnotationServiceTest extends ServiceTestBase {

    @Test
    public void testAnnotate() throws IOException, URISyntaxException {
        final URI uri = UriBuilder.fromResource(DefaultAnnotationService.class)
                .uri(new URI(String.format("http://%s", HOST))).port(PORT)
                .path("resource/" + URLEncoder.encode("http://en.wikipedia.org/wiki/Albert_Einstein", "UTF8")).build();
        final InputStream is = uri.toURL().openStream();
        final BufferedReader br = new BufferedReader( new InputStreamReader(is) );
        final StringBuilder content = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            content.append(line);
        }
        final JsonNode node = JSONUtils.parseJSON(content.toString());
        Assert.assertEquals(2, node.size());
    }

}
