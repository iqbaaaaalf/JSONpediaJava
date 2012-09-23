package com.machinelinking.pagestruct;

import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.serializer.Serializer;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextSerializerHandlerTest extends WikiTextSerializerHandlerTestBase {

    public WikiTextSerializerHandlerTest() {
        super(TestWikiTextSerializerHandler.class);
    }

    @Test
    public void testPageSerialization1() throws IOException, WikiTextParserException {
        verifySerialization("Page1");
    }

    @Test
    public void testPageSerialization2() throws IOException, WikiTextParserException {
        verifySerialization("Page2");
    }

    @Test
    public void testTemplateSerialization1() throws IOException, WikiTextParserException {
        verifySerialization("Template1");
    }

    @Test
    public void testBrokenTemplateSerialization1() throws IOException, WikiTextParserException {
        verifySerialization("BrokenTemplate1");
    }

    @Test
    public void testTableSerialization1() throws IOException, WikiTextParserException {
        verifySerialization("serialization/Table1");
    }

    @Test
    public void testTableSerialization2() throws IOException, WikiTextParserException {
        verifySerialization("serialization/Table2");
    }

    @Test
    public void testTableSerialization3() throws IOException, WikiTextParserException {
        verifySerialization("serialization/Table3");
    }

    public static class TestWikiTextSerializerHandler extends WikiTextSerializerHandler {

        public TestWikiTextSerializerHandler(Serializer serializer) {
            super(serializer);
        }

        @Override
        public String getTemplateId(String templateName) {
            return templateName.trim();
        }
    }

}
