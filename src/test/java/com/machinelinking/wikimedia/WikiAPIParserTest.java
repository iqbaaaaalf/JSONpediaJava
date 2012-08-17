package com.machinelinking.wikimedia;

import junit.framework.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiAPIParserTest {

    @Test
    public void testParseAPIResponse() throws IOException, SAXException {
        final WikiPage page = WikiAPIParser.parseAPIResponse(
                WikimediaUtils.entityToWikiTextURLAPI( new URL("http://en.wikipedia.org/Albert_Einstein") )
        );
        System.out.println(page);
        Assert.assertNotNull(page);
        Assert.assertEquals("Albert Einstein", page.getTitle());
        Assert.assertEquals(736, page.getId());
        Assert.assertTrue(page.getContent().trim().length() > 0);
    }

}
