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

package com.machinelinking.pagestruct;

import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.serializer.Serializer;
import org.junit.Test;

import java.io.IOException;

/**
 * Test case for {@link WikiTextSerializerHandler}.
 *
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
        verifySerialization("Table1");
    }

    @Test
    public void testTableSerialization2() throws IOException, WikiTextParserException {
        verifySerialization("Table2");
    }

    @Test
    public void testTableSerialization3() throws IOException, WikiTextParserException {
        verifySerialization("Table3");
    }

    public static class TestWikiTextSerializerHandler extends WikiTextSerializerHandler {

        public TestWikiTextSerializerHandler(Serializer serializer) {
            super(serializer);
        }

    }

}
