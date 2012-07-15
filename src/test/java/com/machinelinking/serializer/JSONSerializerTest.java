package com.machinelinking.serializer;

import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONSerializerTest {

    private ByteArrayOutputStream baos;
    private Serializer serializer;

    @Before
    public void setUp() throws IOException {
        baos = new ByteArrayOutputStream();
        serializer = new JSONSerializer(baos);
    }

    @After
    public void tearDown() {
        baos = null;
        serializer = null;
    }

    @Test
    public void testOpenObject() {
        serializer.openObject();
        verify("{}");
    }

    @Test
    public void testOpenObject2() {
        serializer.openObject();
        serializer.openObject();
        verify("{ \"NO NAME\" : {} }");
    }

    @Test
    public void testOpenObject3() {
        serializer.openList();
        serializer.openObject();
        verify("[{}]");
    }

    @Test
    public void testOpenObject4() {
        serializer.openList();
        serializer.field("f1");
        serializer.openObject();
        verify("[ { \"f1\" : null } , {} ]");
    }

    @Test
    public void testOpenList() {
        serializer.openList();
        verify("[]");
    }

    @Test
    public void testOpenList2() {
        serializer.openList();
        serializer.openList();
        verify("[[]]");
    }

    @Test
    public void testSpuriousField() {
        serializer.openList();
        serializer.field("f1");
        verify("[{ \"f1\" : null }]");
    }

    @Test
    public void testSpuriousField2() {
        serializer.openList();
        serializer.field("f1");
        serializer.field("f2");
        verify("[ { \"f1\" :  null } , { \"f2\" : null } ]");
    }

    @Test
    public void testSpuriousField3() {
        serializer.openList();
        serializer.field("f1");
        serializer.field("f2");
        serializer.value("v1");
        verify("[{ \"f1\" : null } , { \"f2\" : \"v1\" } ]");
    }

    @Test
    public void testSpuriousValue() {
        serializer.openObject();
        serializer.value("v1");
        verify("{ \"NO FIELD\" : \"v1\" }");
    }

    @Test
    public void testSpuriousValue2() {
        serializer.openObject();
        serializer.value("v1");
        serializer.value("v2");
        verify("{ \"NO FIELD\" : \"v1\" , \"NO FIELD\" : \"v2\" }");
    }

    @Test
    public void testPendingObject() {
        serializer.openObject();
        serializer.field("f1");
        serializer.value("v1");
        serializer.field("f2");
        serializer.closeObject();
        verify("{ \"f1\" : \"v1\" , \"f2\" : null }");
    }

    @Test
    public void testObjectSequence() {
        serializer.openObject();
        serializer.field("f1");
        serializer.value("v1");
        serializer.field("f2");
        serializer.value("v2");
        serializer.closeObject();
        verify("{ \"f1\" : \"v1\" , \"f2\" : \"v2\" }");
    }

    @Test
    public void testListSequence() {
        serializer.openList();
        serializer.value("v1");
        serializer.value("v2");
        serializer.value("v3");
        serializer.closeList();
        verify("[ \"v1\" , \"v2\" , \"v3\" ]");
    }

    @Test
    public void testNestedSequence1() {
        serializer.openObject();
        serializer.field("f1");
        serializer.openObject();
        verify("{ \"f1\" : {} }");
    }

    @Test
    public void testNestedSequence2() {
        serializer.openObject();
        serializer.field("f1");
        serializer.openList();
        verify("{ \"f1\" : [] }");
    }

    @Test
    public void testNestedSequence3() {
        serializer.openList();
        serializer.field("f1");
        serializer.openList();
        verify("[ { \"f1\" : null }, [] ]");
    }

    @Ignore
    @Test
    public void testFieldMultiValue() {
        serializer.openObject();
        serializer.field("f1");
        serializer.value("v1");
        serializer.value("v2");
        verify("{ \"f1\" : [\"v1\", \"v2\"] }");
    }

    private void verify(String expected) {
        serializer.close();
        final String out = baos.toString();
        System.out.println("BAOS: " + out);
        try {
            final JsonNode expectedNode = JSONUtils.parseJSON(expected);
            final JsonNode outNode = JSONUtils.parseJSON(out);
            Assert.assertEquals(expectedNode, outNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
