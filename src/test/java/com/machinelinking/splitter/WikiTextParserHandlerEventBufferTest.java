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

package com.machinelinking.splitter;

import com.machinelinking.pagestruct.WikiTextHRDumperHandler;
import com.machinelinking.parser.WikiTextParserHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Test case for {@link WikiTextParserHandlerEventBuffer}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextParserHandlerEventBufferTest {

    private WikiTextParserHandlerEventBuffer buffer;

    @BeforeMethod
    public void setUp() {
        buffer = new WikiTextParserHandlerEventBuffer();
    }

    @Test
    public void testBuffer() throws MalformedURLException {
        final WikiTextParserHandler handler = buffer.getProxy();
        handler.beginDocument( new URL("http://some.doc"));
        handler.beginTemplate(new WikiTextParserHandler.TemplateName("T1"));
        handler.parameter("p1");
        handler.text("t1");
        handler.text("t2");
        handler.text("t4");
        handler.endTemplate(new WikiTextParserHandler.TemplateName("T1"));
        handler.endDocument();

        Assert.assertEquals(buffer.size(), 8);
        final WikiTextHRDumperHandler serializer = new WikiTextHRDumperHandler();
        buffer.flush(serializer);
        Assert.assertEquals(
                serializer.getContent(),

                "Begin Document\n" +
                "Begin Template: T1\n" +
                "k: p1\n" +
                "Text: 't1'\n" +
                "Text: 't2'\n" +
                "Text: 't4'\n" +
                "End Template: T1\n" +
                "End Document\n"
        );
    }

}
