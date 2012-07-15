package com.machinelinking.parser;

import junit.framework.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TagStripReaderTest {

    @Test
    public void testStripNoTag() throws IOException {
        final String IN = "A B C D 1 2 3 4 5";
        checkStrip(IN, IN);
    }

    @Test
    public void testStripIncompleteTag() throws IOException {
        final String IN = "A > B C D < 1 2 3 4 5";
        checkStrip(  IN,  "A > B C D ");
    }

    @Test
    public void testStripOpenTag() throws IOException {
        final String IN = "outside <tag1> inside";
        checkStrip(  IN,  "outside ");
    }

    @Test
    public void testStripCompleteTag() throws IOException {
        final String IN = "outside 1 <tag1>inside</tag1> outside 2";
        checkStrip(  IN,  "outside 1  outside 2");
    }

    @Test
    public void testStripNestedTags() throws IOException {
        final String IN = "outside 1<tag1>inside 1 pre<tag2>inside 2</tag2>inside 1 post</tag1>outside 2";
        checkStrip(  IN,  "outside 1outside 2");
    }

    @Test
    public void testStripNestedTagsMess() throws IOException {
        final String IN = "outside 1<tag1>inside 1 pre<tag2/>inside 2<tag3>inside 1 post</tag1>outside 2";
        checkStrip(  IN,  "outside 1inside 2outside 2");
    }

    private void checkStrip(String in, String exp) throws IOException {
        final TagStripReader reader = new TagStripReader(
                new InputStreamReader( new ByteArrayInputStream(in.getBytes()) )
        );

        final BufferedReader br = new BufferedReader(reader);
        final String line = br.readLine();
        Assert.assertEquals(exp, line);
    }

}
