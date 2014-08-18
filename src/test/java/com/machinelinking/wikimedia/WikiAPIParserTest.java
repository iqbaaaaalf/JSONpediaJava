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

import junit.framework.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

/**
 * Test case for {@link WikiAPIParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiAPIParserTest {

    @Test
    public void testParseAPIResponse() throws IOException, SAXException {
        final WikiPage page = WikiAPIParser.parseAPIResponse(
                WikimediaUtils.entityToWikiTextURLAPI( new URL("http://en.wikipedia.org/Albert_Einstein") )
        );
        Assert.assertNotNull(page);
        Assert.assertEquals("Albert Einstein", page.getTitle());
        Assert.assertEquals(736, page.getId());
        Assert.assertTrue(page.getContent().trim().length() > 0);
    }

}
