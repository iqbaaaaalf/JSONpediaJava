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

import com.machinelinking.serializer.JSONSerializer;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Test case for {@link JSONUtils}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONUtilsTest {

    @Test
    public void testParseJSONMulti() throws IOException {
         Assert.assertEquals(JSONUtils.parseJSONMulti("{}").length, 1);
         Assert.assertEquals(JSONUtils.parseJSONMulti("{}{}").length, 2);
         Assert.assertEquals(JSONUtils.parseJSONMulti("{}{}{}").length, 3);
    }

    @Test(expectedExceptions = JsonParseException.class)
    public void testParseJSONMultiFail1() throws IOException {
         Assert.assertEquals(JSONUtils.parseJSONMulti("{}{").length, 1);
    }

    @Test(expectedExceptions = JsonParseException.class)
    public void testParseJSONMultiFail2() throws IOException {
         Assert.assertEquals(JSONUtils.parseJSONMulti("{}x{}").length, 1);
    }

    @Test
    public void testJacksonNodeToSerializer() throws IOException {
        final String IN = "{ \"list\" : [\"v1\", \"2\", {\"k1\" : \"v1\"}], \"obj\" : { \"k2\" : [\"v2\"] } }";
        final JsonNode node = JSONUtils.parseJSON(IN);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer serializer  = new JSONSerializer(baos);
        JSONUtils.jacksonNodeToSerializer(node, serializer);
        serializer.close();
        Assert.assertEquals(JSONUtils.parseJSON(baos.toString()), node);
    }

    @Test
    public void testJSONMapArrayToSerializer() throws IOException {
        checkJSONMapArrayToSerializer(
                new Object[]{1, 2, "x"},
                "[1,2,\"x\"]"
        );
        checkJSONMapArrayToSerializer(
                new HashMap<String,Object>(){{ put("k1", new Object[]{1, 2, "x"}); }},
                "{\"k1\":[1,2,\"x\"]}"
        );
    }

    private void checkJSONMapArrayToSerializer(Object in, String expected) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer serializer = new JSONSerializer(baos);
        JSONUtils.jsonMapArrayToSerializer(in, serializer);
        serializer.close();
        Assert.assertEquals(baos.toString(), expected);
    }

}
