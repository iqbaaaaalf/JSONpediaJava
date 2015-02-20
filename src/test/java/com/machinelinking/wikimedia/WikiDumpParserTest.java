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

package com.machinelinking.wikimedia;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Test case for {@link WikiDumpParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiDumpParserTest {

    @Test
    public void testParseDump1() throws IOException, SAXException {
        parseAndVerifyDump("/dumps/enwiki-latest-pages-articles-p1.xml.gz", 10);
    }

    @Test
    public void testParseDump2() throws IOException, SAXException {
        parseAndVerifyDump("/dumps/enwiki-latest-pages-articles-p2.xml.gz", 23);
    }

    @Test
    public void testParseDump3() throws IOException, SAXException {
        parseAndVerifyDump("/dumps/enwiki-latest-pages-articles-p3.xml.gz", 25);
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
        Assert.assertEquals(count, expected);
    }

}
