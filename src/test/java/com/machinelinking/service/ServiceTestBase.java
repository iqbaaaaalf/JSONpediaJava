package com.machinelinking.service;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.After;
import org.junit.Before;

import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ServiceTestBase {

    private final BasicServer server = new BasicServer();

    @Before
    public void setUp() throws IOException {
        server.setUp();
    }

    @After
    public void tearDown() {
        server.tearDown();
    }

    protected UriBuilder buildPath(Class<? extends Service> clazz, String path) throws URISyntaxException {
        return UriBuilder.fromResource(clazz)
            .uri(new URI(String.format("http://%s", BasicServer.DEFAULT_HOST))).port(BasicServer.DEFAULT_PORT)
            .path(path);
    }

    protected JsonNode performQuery(URI uri) throws IOException {
        try (final InputStream is = uri.toURL().openStream()) {
            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            final StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            return JSONUtils.parseJSON(content.toString());
        }
    }

}
