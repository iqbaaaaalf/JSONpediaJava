package com.machinelinking.parser;

import com.machinelinking.pagestruct.WikiTextHRDumperHandler;
import junit.framework.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TagReaderTest {

    @Test
    public void testAttributeValueScanner() {
        final StringBuilder value = new StringBuilder();

        value.delete(0, value.length());
        TagReader.attributeValueScanner("v1 post", 0, value);
        Assert.assertEquals("v1",value.toString());

        value.delete(0, value.length());
        TagReader.attributeValueScanner("\"v1 v2\" post", 0, value);
        Assert.assertEquals("v1 v2", value.toString());
    }

    @Test
    public void testAttributeKeyValueScanner() {
        WikiTextParserHandler.Attribute[] attributes;

        attributes = TagReader.attributeKeyValueScanner("k1 = v1");
        Assert.assertEquals("[k1 : 'v1']", Arrays.asList(attributes).toString());

        attributes = TagReader.attributeKeyValueScanner("k1 = \"v1a v1b\"");
        Assert.assertEquals("[k1 : 'v1a v1b']", Arrays.asList(attributes).toString());

        attributes = TagReader.attributeKeyValueScanner("k1=v1 k2 = v2 k3 = \"v3\" k4 = \"v4a v4b\"");
        Assert.assertEquals("[k1 : 'v1', k2 : 'v2', k3 : 'v3', k4 : 'v4a v4b']", Arrays.asList(attributes).toString());
    }

    @Test
    public void testReadOpenTag() throws IOException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader reader = new TagReader(handler);
        reader.readNode(new TestParserReader("<node attr1=v1 attr2=\"v2\" attr3=\"part1 part2\">"));
        Assert.assertEquals("Open Tag: node attributes: [attr1 : 'v1', attr2 : 'v2', attr3 : 'part1 part2']\n", handler.getContent());
        Assert.assertTrue(reader.isInsideNode());
    }

    @Test
    public void testReadCloseTag() throws IOException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler(false);
        final TagReader reader = new TagReader(handler);
        reader.pushTag("node", new WikiTextParserHandler.Attribute[0]);
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
        tagReader.pushTag("br", new WikiTextParserHandler.Attribute[0]);
        tagReader.pushTag("ref", new WikiTextParserHandler.Attribute[0]);
        tagReader.pushTag("br", new WikiTextParserHandler.Attribute[0]);
        tagReader.popTag("ref");

        final List<TagReader.StackElement> stack = tagReader.getStack();
        Assert.assertEquals("[node: br attributes: []]", stack.toString());
    }

    private class TestParserReader implements ParserReader {

        private final InputStreamReader reader;

        private TestParserReader(String in) {
            this.reader = new InputStreamReader(new ByteArrayInputStream(in.getBytes()));
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
            return null;
        }
    }

}
