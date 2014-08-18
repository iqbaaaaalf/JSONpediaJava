package com.machinelinking.main;

import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import java.util.Map;

/**
 * Test case for {@link com.machinelinking.main.JSONpedia}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONpediaTest {

    @Test
    public void testProcessEntityById() throws JSONpediaException {
        final JsonNode root = JSONpedia.instance().process("en:Albert Einstein").json();
        Assert.assertEquals(8, root.size());
    }

    @Test
    public void testProcessEntityByURL() throws JSONpediaException {
        final JsonNode root = JSONpedia.instance().process("http://en.wikipedia.org/wiki/Albert_Einstein").json();
        Assert.assertEquals(8, root.size());
    }

    @Test
    public void testProcessEntityAsMap() throws JSONpediaException {
        final Map<String,?> root = JSONpedia.instance().process("en:Albert Einstein").map();
        Assert.assertEquals(8, root.size());
    }

    @Test
    public void testProcessEntityAsHTML() throws JSONpediaException {
        final String html = JSONpedia.instance().process("en:Albert Einstein").html();
        Assert.assertTrue(html.length() > 1000);
    }

    @Test
    public void testProcessWikiText() throws JSONpediaException {
        final JsonNode root = JSONpedia.instance()
                .process("en:Albert Einstein")
                .text("A really ''short'' description of Albert Einstein")
                .json();
        Assert.assertEquals(8, root.size());
    }

    @Test
    public void testProcessEntityWithFlags() throws JSONpediaException {
        final JsonNode root = JSONpedia.instance()
                .process("en:Albert Einstein").flags("Linkers,Validate,Structure").json();
        Assert.assertEquals(11, root.size());
    }

    @Test
    public void testProcessEntityWithFilter() throws JSONpediaException {
        final JsonNode root = JSONpedia.instance()
                .process("en:Albert Einstein").flags("Linkers,Validate,Structure").filter("__type:reference").json();
        Assert.assertEquals(2, root.size());
    }

}
