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

    public static final String FILTER_EXP = "name:Death date and age,__type:template";

    @Test
    public void testParseFilter() throws IOException {
        final JSONFilter r = DefaultJSONFilterEngine.parseFilter(FILTER_EXP);
        Assert.assertEquals(
                "__type=template\n" +
                "name=Death date and age\n",
                r.print()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFilterFail() throws IOException {
        DefaultJSONFilterEngine.parseFilter("name::fake");
    }

    @Test
    public void testApplyFilter() throws IOException {
        final JsonNode[] r = DefaultJSONFilterEngine.applyFilter(loadJSON(), FILTER_EXP);
        Assert.assertEquals(2, r.length);
    }

    @Test
    public void testFilter1() throws IOException {
        final JSONFilter filter = new DefaultJSONFilter();
        filter.addCriteria("__type", "template");
        filter.addCriteria("name"  , "Death date and age");
        checkFilter(filter);
    }

    @Test
    public void testFilter2() throws IOException {
        final JSONFilter filter = new DefaultJSONFilter();
        final JSONFilterParser parser = new DefaultJSONFilterParser();
        parser.parse(FILTER_EXP, filter);
        checkFilter(filter);
    }

    private JsonNode loadJSON() throws IOException {
        return JSONUtils.parseJSON(this.getClass().getResourceAsStream("/Enrichment1.json"));
    }

    private void checkFilter(JSONFilter filter) throws IOException {
        final JsonNode node = loadJSON();

        final JSONFilterEngine engine = new DefaultJSONFilterEngine();
        final JsonNode[] result = engine.filter(node, filter);

        Assert.assertEquals(2, result.length);
    }

}
