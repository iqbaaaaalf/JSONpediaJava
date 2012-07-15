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
        final String output = WikiAPIParser.parseAPIResponse(
                WikimediaUtils.entityToWikiTextURLAPI( new URL("http://en.wikipedia.org/Albert_Einstein") )
        );
        System.out.println(output);
        Assert.assertNotNull(output);
        Assert.assertTrue(output.trim().length() > 0);
    }

}
