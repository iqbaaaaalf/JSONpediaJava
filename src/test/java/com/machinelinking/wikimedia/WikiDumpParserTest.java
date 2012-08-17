package com.machinelinking.wikimedia;

import junit.framework.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiDumpParserTest {

    @Test
    public void testParseDump1() throws IOException, SAXException {
        parseAndVerifyDump("/enwiki-latest-pages-articles-p1.xml.gz", 10);
    }

    @Test
    public void testParseDump2() throws IOException, SAXException {
        parseAndVerifyDump("/enwiki-latest-pages-articles-p2.xml.gz", 23);
    }

    @Test
    public void testParseDump3() throws IOException, SAXException {
        parseAndVerifyDump("/enwiki-latest-pages-articles-p3.xml.gz", 25);
    }

    private void parseAndVerifyDump(String dump, int expected) throws IOException, SAXException {
        final WikiDumpParser dumpParser = new WikiDumpParser();
        final BufferedWikiPageHandler bufferedHandler = new BufferedWikiPageHandler();
        dumpParser.parse(
                bufferedHandler,
                new GZIPInputStream( this.getClass().getResourceAsStream(dump) )
        );

        int count = 0;
        WikiPage page;
        while( (page = bufferedHandler.getPage(false)) != BufferedWikiPageHandler.EOQ) {
            Assert.assertTrue(page.getId() > 1);
            Assert.assertTrue(page.getTitle().trim().length() > 0);
            Assert.assertTrue(page.getContent().trim().length() > 0);
            count++;
        }
        Assert.assertEquals(expected, count);
    }

}
