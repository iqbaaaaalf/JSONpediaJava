package com.machinelinking.parser;

import com.machinelinking.pagestruct.WikiTextHRDumperHandler;
import junit.framework.Assert;
import org.junit.Test;

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
                "Open Tag: node attributes: [attr1 : 'v1', attr2 : 'v2', attr3 : 'part1 part2']\n",
                handler.getContent()
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
                "Open Tag: node attributes: []\n" +
                "Close Tag: node\n",
                handler.getContent()
        );
        Assert.assertFalse(reader.isInsideNode());
    }

    @Test
    public void testReadInlineTag() throws IOException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader reader = new TagReader(handler);
        reader.readNode(new TestParserReader("<node a1=v1 a2=\"v2\"/>"));
        Assert.assertEquals(
                "Inline Tag: node attributes: [a1 : 'v1', a2 : 'v2']\n",
                handler.getContent()
        );
        Assert.assertFalse(reader.isInsideNode());
    }

    @Test
    public void testReadCommentTag() throws IOException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader reader = new TagReader(handler);
        reader.readNode(new TestParserReader("<!-- ISO format (YYYY-MM-DD) -->"));
        Assert.assertEquals(
                "Comment Tag:  ISO format (YYYY-MM-DD) \n",
                handler.getContent()
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
        Assert.assertEquals("[node: br attributes: []]", stack.toString());
    }

    @Test
    public void testInvalidCloseSequenceDetection() throws IOException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader reader = new TagReader(handler);
        reader.readNode(new TestParserReader("<]]\n|-\n"));
        Assert.assertEquals(
                "",
                handler.getContent()
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
