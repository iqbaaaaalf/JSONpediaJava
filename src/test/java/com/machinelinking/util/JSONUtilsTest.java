package com.machinelinking.util;

import com.machinelinking.serializer.JSONSerializer;
import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONUtilsTest {

    @Test
    public void testSerializeNode() throws IOException {
        final String IN = "{ \"list\" : [\"v1\", \"2\", {\"k1\" : \"v1\"}], \"obj\" : { \"k2\" : [\"v2\"] } }";
        final JsonNode node = JSONUtils.parseJSON(IN);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer serializer  = new JSONSerializer(baos);
        JSONUtils.jacksonNodeToSerializer(node, serializer);
        serializer.close();
        System.out.println(baos.toString());
        Assert.assertEquals(node, JSONUtils.parseJSON(baos.toString()));
    }

}
