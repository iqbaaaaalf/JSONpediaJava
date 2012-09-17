package com.machinelinking.splitter;

import com.machinelinking.pagestruct.WikiTextHRDumperHandler;
import com.machinelinking.parser.WikiTextParserHandler;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextParserHandlerEventBufferTest {

    private WikiTextParserHandlerEventBuffer buffer;

    @Before
    public void setUp() {
        buffer = new WikiTextParserHandlerEventBuffer();
    }

    @Test
    public void testBuffer() throws MalformedURLException {
        final WikiTextParserHandler handler = buffer.getProxy();
        handler.beginDocument( new URL("http://some.doc"));
        handler.beginTemplate("T1");
        handler.parameter("p1");
        handler.text("t1");
        handler.text("t2");
        handler.text("t4");
        handler.endTemplate("T1");
        handler.endDocument();

        Assert.assertEquals(8, buffer.size());
        final WikiTextHRDumperHandler serializer = new WikiTextHRDumperHandler();
        buffer.flush(serializer);
        Assert.assertEquals(
                "Begin Document\n" +
                "Begin Template: T1\n" +
                "k: p1\n" +
                "Text: 't1'\n" +
                "Text: 't2'\n" +
                "Text: 't4'\n" +
                "End Template: T1\n" +
                "End Document\n",

                serializer.getContent()
        );
    }

}
