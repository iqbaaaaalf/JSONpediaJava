package com.machinelinking.filter;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Test case for {@link DefaultJSONFilterEngine}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONFilterEngineTest {

    public static final String BASIC_FILTER_EXP = "name:Death date and age,__type:template";
    public static final String COMPLEX_FILTER_EXP = "url:\".*[\\s,\\d]?\\.html\",__type:link";

    @Test
    public void testParseFilter1() throws IOException {
        final JSONFilter r = DefaultJSONFilterEngine.parseFilter(BASIC_FILTER_EXP);
        Assert.assertEquals(
                "__type=template\n" +
                "name=Death date and age\n",
                r.print()
        );
    }

    @Test
    public void testParseFilter2() throws IOException {
        final JSONFilter r = DefaultJSONFilterEngine.parseFilter(COMPLEX_FILTER_EXP);
        Assert.assertEquals(
                "__type=link\n" +
                "url=.*[\\s,\\d]?\\.html\n",
                r.print()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFilterFail() throws IOException {
        DefaultJSONFilterEngine.parseFilter("name::fake");
    }

    @Test
    public void testFilterCriteria() throws IOException {
        final JSONFilter filter = new DefaultJSONFilter();
        filter.addCriteria("__type", "template");
        filter.addCriteria("name"  , "Death date and age");
        checkFilter(filter, 2);
    }

    @Test
    public void testFilterCriteriaRegex() throws IOException {
        final JSONFilter filter = new DefaultJSONFilter();
        filter.addCriteria("__type", "template");
        filter.addCriteria("name"  , "Death .{1,4} and age");
        checkFilter(filter, 2);
    }

    @Test
    public void testFilter1() throws IOException {
        final JSONFilter filter = new DefaultJSONFilter();
        final JSONFilterParser parser = new DefaultJSONFilterParser();
        parser.parse(BASIC_FILTER_EXP, filter);
        checkFilter(filter, 2);
    }

    @Test
    public void testFilter2() throws IOException {
        final JSONFilter filter = new DefaultJSONFilter();
        final JSONFilterParser parser = new DefaultJSONFilterParser();
        parser.parse(COMPLEX_FILTER_EXP, filter);
        checkFilter(filter, 9);
    }

    @Test
    public void testEngineApply() throws IOException {
        final JsonNode[] r = DefaultJSONFilterEngine.applyFilter(loadJSON(), BASIC_FILTER_EXP);
        Assert.assertEquals(2, r.length);
    }

    private JsonNode loadJSON() throws IOException {
        return JSONUtils.parseJSON(this.getClass().getResourceAsStream("Data.json"));
    }

    private void checkFilter(JSONFilter filter, final int EXPECTED) throws IOException {
        final JsonNode node = loadJSON();

        final JSONFilterEngine engine = new DefaultJSONFilterEngine();
        final JsonNode[] result = engine.filter(node, filter);

        Assert.assertEquals(EXPECTED, result.length);
    }

}
