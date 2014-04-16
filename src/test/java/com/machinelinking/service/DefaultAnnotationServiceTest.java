package com.machinelinking.service;

import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.filter.DefaultJSONFilterEngineTest;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
            TARGET_RESOURCE = "resource/json/" + URLEncoder.encode(
                    "http://en.wikipedia.org/wiki/Albert_Einstein", "UTF8"
            );
        } catch (UnsupportedEncodingException urise) {
            throw new IllegalStateException();
        }
    }

    @Test
    public void testFlags() throws IOException, URISyntaxException {
        final JsonNode node = performQuery(buildPath(DefaultAnnotationService.class, "flags").build());
        Assert.assertEquals(
                WikiEnricherFactory.getInstance().getDefinedFlags().length,
                node.get("definedFlags").size()
        );
    }

    @Test
    public void testAnnotate() throws IOException, URISyntaxException {
        checkJSONResponse( performQuery(buildPath(DefaultAnnotationService.class, TARGET_RESOURCE).build()) );
    }

    @Test
    public void testAnnotateOnline() throws IOException, URISyntaxException {
        final JsonNode node = performQuery(
                buildPath(DefaultAnnotationService.class, TARGET_RESOURCE)
                    .queryParam("procs", WikiEnricherFactory.Linkers).build()
        );
        checkJSONResponse(node);
        Assert.assertNotNull(node.get("freebase"));
    }

    @Test
    public void testAnnotateWithFilters() throws URISyntaxException, IOException {
        final JsonNode node = performQuery(
                buildPath(DefaultAnnotationService.class, TARGET_RESOURCE)
                        .queryParam("procs", WikiEnricherFactory.Structure)
                        .queryParam("filter", DefaultJSONFilterEngineTest.STRING_FILTER_EXP).build()
        );
        Assert.assertEquals(
                JSONUtils.parseJSON(
                        "{\"filter\":\"object_filter(__type=template,name=Death date and age,)>null\"," +
                                "\"result\":[{\"__type\":\"template\",\"name\":\"Death date and age\"," +
                                "\"content\":{\"df\":[\"yes\"],\"__an0\":[\"1955\"],\"__an1\":[\"4\"]," +
                                "   \"__an2\":[\"18\"],\"__an3\":[\"1879\"],\"__an4\":[\"3\"],\"__an5\":[\"14\"]}}]}"
                ).toString(),
                node.toString()
        );
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
