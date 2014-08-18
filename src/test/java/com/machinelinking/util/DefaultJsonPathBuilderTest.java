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

package com.machinelinking.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Test case for {@link DefaultJsonPathBuilder}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJsonPathBuilderTest {

    private JsonPathBuilder jsonPathBuilder = new DefaultJsonPathBuilder();

    @Test
    public void testPath() {
        jsonPathBuilder.enterObject();
        Assert.assertEquals("$", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.field("fieldA");
        jsonPathBuilder.enterArray();
        Assert.assertEquals("$.fieldA[*]", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.arrayElem();
        Assert.assertEquals("$.fieldA[0]", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.arrayElem();
        Assert.assertEquals("$.fieldA[1]", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.enterObject();
        Assert.assertEquals("$.fieldA[1]", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.field("fieldB");
        Assert.assertEquals("$.fieldA[1].fieldB", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.enterObject();
        Assert.assertEquals("$.fieldA[1].fieldB", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.field("fieldC");
        Assert.assertEquals("$.fieldA[1].fieldB.fieldC", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.exitObject();
        Assert.assertEquals("$.fieldA[1].fieldB", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.exitObject();
        Assert.assertEquals("$.fieldA[1]", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.exitArray();
        Assert.assertEquals("$.fieldA", jsonPathBuilder.getJsonPath());
        jsonPathBuilder.exitObject();
    }

    @Test
    public void testMatch() {
        final JsonPathBuilder b1 = new DefaultJsonPathBuilder();
        b1.enterObject();
        b1.field("fi");
        b1.enterObject();
        b1.field("f2");
        b1.enterArray();
        b1.arrayElem();
        b1.arrayElem();

        final JsonPathBuilder b2 = new DefaultJsonPathBuilder();
        b2.enterObject();
        b2.field("fi");
        b2.enterObject();
        b2.field("f2");
        b2.enterArray();
        b2.arrayElem();
        b2.arrayElem();
        b2.enterObject();
        b2.field("f3");

        final JsonPathBuilder b3 = new DefaultJsonPathBuilder();
        b3.enterObject();
        b3.field("fi");
        b3.enterObject();
        b3.field("fX");
        b3.enterArray();
        b3.arrayElem();
        b3.arrayElem();
        b3.enterObject();
        b3.field("f3");

        Assert.assertTrue(
                String.format("Invalid match: %s doesn't contain %s", b2.getJsonPath(), b1.getJsonPath()),
                b2.subPathOf(b1, false)
        );

        Assert.assertFalse(
                b2.subPathOf(b1, true)
        );

        Assert.assertFalse(
                String.format("Invalid match: %s should not contain %s", b1.getJsonPath(), b3.getJsonPath()),
                b3.subPathOf(b1, true)
        );

    }

}
