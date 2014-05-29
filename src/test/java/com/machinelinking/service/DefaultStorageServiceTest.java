package com.machinelinking.service;

import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * Test case for {@link com.machinelinking.service.DefaultStorageService}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultStorageServiceTest extends ServiceTestBase {

    private static final String MAP_FUNC;
    private static final String RED_FUNC;

    static {
        try {
            MAP_FUNC = URLEncoder.encode(
                    "function() {  ocs = this.content.templates.occurrences; for(template in ocs) { emit(template, ocs[template]); } }",
                    "utf8"
            );
            RED_FUNC = URLEncoder.encode(
                    "function(key, values) { return Array.sum(values) }",
                    "utf8"
            );
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException(uee);
        }
    }

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
    public void testMongoSelect() throws URISyntaxException, IOException, ConnectionException {
        final JsonNode output = performQuery(
            buildPath(DefaultStorageService.class, "mongo/select")
                    .queryParam("q", "name = doc_1 -> _id")
                    .queryParam("limit", Integer.toString(10))
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
    public void testMongoSelectInvalidParams() throws URISyntaxException, IOException {
        performQueryAndCheckError(
                400,
                buildPath(DefaultStorageService.class, "mongo/select")
                        .queryParam("q", "bleah")
                        .queryParam("limit", "10")
                        .build()
        );

        performQueryAndCheckError(
                400,
                buildPath(DefaultStorageService.class, "mongo/select")
                        .queryParam("q", "name = doc_1 -> _id")
                        .queryParam("limit", "x")
                        .build()
        );
    }

    @Test
    public void testMongoMapRed() throws URISyntaxException, IOException, ConnectionException {
        final JsonNode output = performQuery(
            buildPath(DefaultStorageService.class, "mongo/mapred")
                    .queryParam("criteria", "")
                    .queryParam("map", MAP_FUNC)
                    .queryParam("reduce", RED_FUNC)
                    .queryParam("limit", Integer.toString(10))
                    .build()
        );

        Assert.assertTrue(output.get("result").size() >= 1);
    }

    @Test
    public void testMongoMapRedInvalidParams() throws URISyntaxException, IOException {
        performQueryAndCheckError(
                400,
                buildPath(DefaultStorageService.class, "mongo/mapred")
                        .queryParam("criteria", "X")
                        .queryParam("map", MAP_FUNC)
                        .queryParam("reduce", RED_FUNC)
                        .build()
        );

        performQueryAndCheckError(
                400,
                buildPath(DefaultStorageService.class, "mongo/mapred")
                        .queryParam("criteria", "")
                        .queryParam("map", MAP_FUNC)
                        .queryParam("reduce", RED_FUNC)
                        .queryParam("limit", "x")
                        .build()
        );

        performQueryAndCheckError(
                500,
                buildPath(DefaultStorageService.class, "mongo/mapred")
                        .queryParam("criteria", "")
                        .queryParam("map", MAP_FUNC)
                        .queryParam("reduce", "xxx")
                        .build()
        );

        performQueryAndCheckError(
                500,
                buildPath(DefaultStorageService.class, "mongo/mapred")
                        .queryParam("criteria", "")
                        .queryParam("map", "xxx")
                        .queryParam("reduce", RED_FUNC)
                        .build()
        );
    }

}
