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

package com.machinelinking.parser;

import com.machinelinking.pagestruct.WikiTextHRDumperHandler;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Test case for {@link TagReader}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TagReaderTest {

    @Test
    public void testReadOpenTag() throws IOException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader reader = new TagReader(handler);
        reader.readNode(new TestParserReader("<node attr1=v1 attr2=\"v2\" attr3=\"part1 part2\">"));
        Assert.assertEquals(
                handler.getContent(),
                "Open Tag: node attributes: [attr1 : 'v1', attr2 : 'v2', attr3 : 'part1 part2']\n"
                );
        Assert.assertTrue(reader.isInsideNode());
    }

    @Test
    public void testReadCloseTag() throws IOException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader reader = new TagReader(handler);
        reader.pushTag("node", new Attribute[0]);
        reader.readNode(new TestParserReader("</node>"));
        Assert.assertEquals(
                handler.getContent(),
                "Open Tag: node attributes: []\n" +
                "Close Tag: node\n"
        );
        Assert.assertFalse(reader.isInsideNode());
    }

    @Test
    public void testReadInlineTag() throws IOException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader reader = new TagReader(handler);
        reader.readNode(new TestParserReader("<node a1=v1 a2=\"v2\"/>"));
        Assert.assertEquals(
                handler.getContent(),
                "Inline Tag: node attributes: [a1 : 'v1', a2 : 'v2']\n"
                );
        Assert.assertFalse(reader.isInsideNode());
    }

    @Test
    public void testReadCommentTag() throws IOException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader reader = new TagReader(handler);
        reader.readNode(new TestParserReader("<!-- ISO format (YYYY-MM-DD) -->"));
        Assert.assertEquals(
                handler.getContent(),
                "Comment Tag:  ISO format (YYYY-MM-DD) \n"
        );
        Assert.assertFalse(reader.isInsideNode());
    }

    @Test
    public void testNodeStack() {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader tagReader = new TagReader(handler);
        tagReader.pushTag("br", new Attribute[0]);
        tagReader.pushTag("ref", new Attribute[0]);
        tagReader.pushTag("br", new Attribute[0]);
        tagReader.popTag("ref", null);

        final List<TagReader.StackElement> stack = tagReader.getStack();
        Assert.assertEquals(stack.toString(), "[node: br attributes: []]");
    }

    @Test
    public void testInvalidCloseSequenceDetection() throws IOException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader reader = new TagReader(handler);
        reader.readNode(new TestParserReader("<]]\n|-\n"));
        Assert.assertEquals(
                handler.getContent(),
                ""
        );
    }

    private class TestParserReader implements ParserReader {

        private final Reader reader;

        private TestParserReader(String in) {
            this.reader = new BufferedReader( new InputStreamReader(new ByteArrayInputStream(in.getBytes())) );
        }

        @Override
        public char read() throws IOException {
            int intc = reader.read();
            if(intc == -1) throw new EOFException();
            return (char) intc;
        }

        @Override
        public void mark() throws IOException {
            reader.mark(500);
        }

        @Override
        public void reset() throws IOException {
            reader.reset();
        }

        @Override
        public ParserLocation getLocation() {
            return new DefaultParserLocation(0, 0);
        }
    }

}
