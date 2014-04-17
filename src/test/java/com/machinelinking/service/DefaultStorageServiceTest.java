package com.machinelinking.service;

import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Test case for {@link com.machinelinking.service.DefaultStorageService}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultStorageServiceTest extends ServiceTestBase {

    @Before
    public void setUp() throws IOException {
        super.setUp();

        if(!ConfigurationManager.getInstance().isInitialized()) {
            final Properties properties = new Properties();
            properties.put(
                    DefaultStorageService.STORAGE_SERVICE_CONNECTION_PROP, "localhost:7654:test_load:test_load_table"
            );
            properties.put(DefaultStorageService.STORAGE_SERVICE_QUERY_LIMIT_PROP, Integer.toString(1000));
            ConfigurationManager.getInstance().initProperties(properties);
        }
    }

    @Test
    public void testQueryStorage() throws URISyntaxException, IOException, ConnectionException {
        final JsonNode output = performQuery(
            buildPath(DefaultStorageService.class, "select")
                    .queryParam("q", "name = doc_1 -> _id")
                    .queryParam("limit", "10")
                    .build()
        );

        Assert.assertEquals(
                "criterias: [name eq 'doc_1'], projections: [content, _id, name, version]",
                output.get("query-explain").asText()
        );
        Assert.assertEquals(1, output.get("count").asInt());
        Assert.assertNotNull(output.get("result"));
        Assert.assertTrue(output.get("result").size() >= 1);
    }

    @Test
    public void testInvalidQueryParam() throws URISyntaxException, IOException {
        performQueryAndCheckError(
                400,
                buildPath(DefaultStorageService.class, "select")
                        .queryParam("q", "bleah")
                        .queryParam("limit", "10")
                        .build()
        );
    }

    @Test
    public void testInvalidLimitParam() throws URISyntaxException, IOException {
        performQueryAndCheckError(
                400,
                buildPath(DefaultStorageService.class, "select")
                        .queryParam("q", "name = doc_1 -> _id")
                        .queryParam("limit", "x")
                        .build()
        );
    }

}
