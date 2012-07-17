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
        checkStrip(  IN,  "outside  inside");
    }

    @Test
    public void testStripCompleteTag() throws IOException {
        final String IN = "outside 1 <tag1>inside</tag1> outside 2";
        checkStrip(  IN,  "outside 1 inside outside 2");
    }

    @Test
    public void testStripNestedTags() throws IOException {
        final String IN = "outside 1<tag1>inside 1 pre<tag2>inside 2</tag2>inside 1 post</tag1>outside 2";
        checkStrip(  IN,  "outside 1inside 1 preinside 2inside 1 postoutside 2");
    }

    @Test
    public void testStripNestedTagsMess() throws IOException {
        final String IN = "outside 1<tag1>inside 1 pre<tag2/>inside 2<tag3>inside 1 post</tag1>outside 2";
        checkStrip(  IN,  "outside 1inside 1 preinside 2inside 1 postoutside 2");
    }

    @Test
    public void testLongStrip() throws IOException {
        final StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 32 * 1024; i++) {
            sb.append((char) ('a' + (i % 26)) );
        }
        System.out.println(sb);
        final String s = sb.toString();
        checkStrip(s, s);
    }

    private void checkStrip(String in, String exp) throws IOException {
        final TagStripReader reader = new TagStripReader(
                new InputStreamReader( new ByteArrayInputStream(in.getBytes()) ),new DefaultWikiTextParserHandler()
        );

        final BufferedReader br = new BufferedReader(reader);
        final String line = br.readLine();
        Assert.assertEquals(exp, line);
    }

}
