package com.machinelinking.parser;

import junit.framework.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Test case for {@link EntityExpansionReader}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class EntityExpansionReaderTest {

    @Test
    public void testNoExpansion() throws IOException {
        checkString("abc def", "abc def");
    }

    @Test
    public void testEmpty() throws IOException {
        checkString(">&#;<", "><");
    }

    @Test
    public void testMissingEnd() throws IOException {
        checkString("&#1234", "&#1234");
    }

    @Test
    public void testStopExpandNonAlphaNumeric() throws IOException {
        checkString("&#12_; &#12 ; &#12';", "&#12_; &#12 ; &#12';");
    }

    @Test
    public void testExpansionWithNumer() throws IOException {
        checkString(">&#1234;<", ">Ӓ<");
    }

    @Test
    public void testExpansionWithHex() throws IOException {
        checkString(">&#x7c;<", ">|<");
        checkString(">&#x70;<", ">p<");
    }

    @Test
    public void testMultiExpansion() throws IOException {
        final String IN = "pre1>&#91;<post1 pre2>&#91;<post2 pre3>&#91;&nbsp;&#x5D;<post3";
        final String EXPECTED = IN
                .replaceAll("&#91;" , "[")
                .replaceAll("&#x5D;", "]")
                .replaceAll("&nbsp;", " ");
        checkString(IN, EXPECTED);
    }

    @Test
    public void testNamedEntityExpansion() throws IOException {
        checkString(">&nbsp;<", "> <");
    }

    @Test
    public void testNeutral() throws IOException {
        checkString("A&TLincoln.jpg", "A&TLincoln.jpg");
    }

    @Test
    public void testReal() throws IOException {
        checkString(
                "Samuel Augustus Ward &#91;hymnal&#93;:Print Material Full Description",
                "Samuel Augustus Ward [hymnal]:Print Material Full Description"
        );
    }

    @Test
    public void testIgnoreMarkup() throws IOException {
        checkString(
                "A link [link value] a ref [[ref value]] a template {{template|x|y|z}} a table {|xx |- yy ! title |- zz|- kk|}",
                "A link [link value] a ref [[ref value]] a template {{template|x|y|z}} a table {|xx |- yy ! title |- zz|- kk|}"
        );
    }

    @Test
    public void testExpansionWithMarkReset() throws IOException {
        final EntityExpansionReader expandableReader = new EntityExpansionReader(
                new BufferedReader(new StringReader("&#93;"))
        );
        expandableReader.mark(1);
        char c;
        c = (char) expandableReader.read();
        Assert.assertEquals(']', c);
        expandableReader.reset();
        c = (char) expandableReader.read();
        Assert.assertEquals(']', c);
    }

    private void checkString(String in, String expected) throws IOException {
        checkStringCharByChar(in, expected);
        checkStringWithBulkBuffer(in, expected);
    }

    private void checkStringCharByChar(String in, String expected) throws IOException {
        final EntityExpansionReader expandableReader = new EntityExpansionReader(
                new BufferedReader(new StringReader(in))
        );
        final char[] charByCharBuffer = new char[expected.length()];
        for(int i = 0; i < charByCharBuffer.length; i++) {
            charByCharBuffer[i] = (char) expandableReader.read();
        }
        final String out = new String(charByCharBuffer);
        System.out.printf("Expected: '%s'\n", expected);
        System.out.printf("Out     : '%s'\n", out);
        Assert.assertEquals(expected, out);
    }

    private void checkStringWithBulkBuffer(String in, String expected) throws IOException {
        final EntityExpansionReader expandableReader = new EntityExpansionReader(
                new BufferedReader(new StringReader(in))
        );
        final char[] bulkBuffer = new char[expected.length()];
        final int readChars     = expandableReader.read(bulkBuffer, 0, bulkBuffer.length);
        final String out = new String(bulkBuffer);
        System.out.printf("Expected: '%s'\n", expected);
        System.out.printf("Out     : '%s'\n", out);
        Assert.assertEquals(expected.length() == 0 ?  -1 : expected.length(), readChars);
        Assert.assertEquals(expected, out);
    }

}
