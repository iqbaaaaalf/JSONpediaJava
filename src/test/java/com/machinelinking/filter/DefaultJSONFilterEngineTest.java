/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

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

    public static final String STRING_FILTER_EXP = "name:Death date and age,@type:template";
    public static final String REGEX_FILTER_EXP  = "url:\".*[\\s,\\d]?\\.html\",@type:link";
    public static final String NESTED_FILTER_EXP = "notable_students>@type:template,name:Plainlist>@type:reference";

    @Test
    public void testParseFilter1() throws IOException {
        final JSONFilter r = DefaultJSONFilterEngine.parseFilter(STRING_FILTER_EXP);
        Assert.assertEquals(
                "object_filter(__type=template,name=Death date and age,)>null",
                r.humanReadable()
        );
    }

    @Test
    public void testParseFilter2() throws IOException {
        final JSONFilter r = DefaultJSONFilterEngine.parseFilter(REGEX_FILTER_EXP);
        Assert.assertEquals(
                "object_filter(__type=link,url=.*[\\s,\\d]?\\.html,)>null",
                r.humanReadable()
        );
    }

    @Test
    public void testParseFilter3() throws IOException {
        final JSONFilter r = DefaultJSONFilterEngine.parseFilter(NESTED_FILTER_EXP);
        Assert.assertEquals(
                "key_filter(notable_students)>" +
                "object_filter(__type=template,name=Plainlist,)>" +
                "object_filter(__type=reference,)>null",
                r.humanReadable()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFilterFail() throws IOException {
        DefaultJSONFilterEngine.parseFilter("name::fake");
    }

    @Test
    public void testFilterCriteria() throws IOException {
        final JSONObjectFilter filter = new DefaultJSONObjectFilter();
        filter.addCriteria("__type", "template");
        filter.addCriteria("name"  , "Death date and age");
        checkFilter(filter, 2);
    }

    @Test
    public void testFilterCriteriaRegex() throws IOException {
        final JSONObjectFilter filter = new DefaultJSONObjectFilter();
        filter.addCriteria("__type", "template");
        filter.addCriteria("name"  , "Death .{1,4} and age");
        checkFilter(filter, 2);
    }

    @Test
    public void testFilterNestedCriteria() throws IOException {
        final JSONKeyFilter notableStudentsFilter = new DefaultJSONKeyFilter();
        notableStudentsFilter.setCriteria("notable_students");

        final JSONObjectFilter plainListFilter = new DefaultJSONObjectFilter();
        plainListFilter.addCriteria("__type", "template");
        plainListFilter.addCriteria("name", "Plainlist");

        final JSONObjectFilter typeFilter = new DefaultJSONObjectFilter();
        typeFilter.addCriteria("__type", "reference");

        notableStudentsFilter.setNested(plainListFilter);
        plainListFilter.setNested(typeFilter);

        checkFilter(notableStudentsFilter, 8); // Some duplicates because test data contain splitter replica.
    }

    @Test
    public void testFilter1() throws IOException {
        final JSONFilterFactory factory = new DefaultJSONFilterFactory();
        final JSONFilterParser parser = new DefaultJSONFilterParser();
        final JSONFilter filter = parser.parse(STRING_FILTER_EXP, factory);
        checkFilter(filter, 2);
    }

    @Test
    public void testFilter2() throws IOException {
        final JSONFilterFactory factory = new DefaultJSONFilterFactory();
        final JSONFilterParser parser = new DefaultJSONFilterParser();
        final JSONFilter filter = parser.parse(REGEX_FILTER_EXP, factory);
        checkFilter(filter, 9);
    }

    @Test
    public void testEngineApply() throws IOException {
        final JsonNode[] r = DefaultJSONFilterEngine.applyFilter(loadJSON(), STRING_FILTER_EXP);
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
