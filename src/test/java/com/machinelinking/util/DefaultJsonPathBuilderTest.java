package com.machinelinking.util;

import junit.framework.Assert;
import org.junit.Test;

/**
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

}
