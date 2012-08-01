package com.machinelinking.filter;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONFilterEngineTest {

    private static final String FILTER_EXP = "name:Death date and age,__type:template";

    @Test
    public void testFilter1() throws IOException {
        final JSONFilter filter = new DefaultJSONFilter();
        filter.addCriteria("__type", "template");
        filter.addCriteria("name"  , "Death date and age");

        applyFilter(filter);
    }

    @Test
    public void testFilter2() throws IOException {
        final JSONFilter filter = new DefaultJSONFilter();
        final JSONFilterParser parser = new DefaultJSONFilterParser();
        parser.parse(FILTER_EXP, filter);
        applyFilter(filter);
    }

    @Test
    public void testFilter3() throws IOException {
        final JsonNode[] r = DefaultJSONFilterEngine.filter(loadJSON(), FILTER_EXP);
        Assert.assertEquals(2, r.length);
    }

    private JsonNode loadJSON() throws IOException {
        return JSONUtils.parseJSON(this.getClass().getResourceAsStream("/Enrichment.json"));
    }

    private void applyFilter(JSONFilter filter) throws IOException {
        final JsonNode node = loadJSON();

        final JSONFilterEngine engine = new DefaultJSONFilterEngine();
        final JsonNode[] result = engine.filter(node, filter);

        Assert.assertEquals(2, result.length);
    }

}
