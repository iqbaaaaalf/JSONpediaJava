package com.machinelinking.splitter;

import com.machinelinking.pagestruct.WikiTextHRDumperHandler;
import com.machinelinking.parser.WikiTextParserHandler;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Test case for {@link WikiTextParserHandlerSplitter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextParserHandlerSplitterTest {

    private WikiTextParserHandlerSplitter splitter;

    @Before
    public void setUp() {
        splitter = new WikiTextParserHandlerSplitter();
    }

    @Test
    public void testSplitting() throws MalformedURLException {
        final String REDIRECTION_NAME = "test-redirection";
        final WikiTextParserHandler proxy = splitter.getProxy();
        proxy.beginDocument( new URL("http://path/to/test") );
        proxy.beginTemplate(new WikiTextParserHandler.TemplateName("t1"));
        proxy.parameter("p1");
        proxy.text("text0");
        proxy.parameter("p2");
        proxy.beginTemplate(new WikiTextParserHandler.TemplateName("tt1"));

        // Redirect from here.
        final WikiTextParserHandlerEventBuffer buffer = splitter.createRedirection(REDIRECTION_NAME);
        Assert.assertEquals(1, splitter.getActiveRedirections().size());
        Assert.assertEquals(REDIRECTION_NAME, splitter.getActiveRedirections().get(0).id);

        proxy.parameter("pp1");
        proxy.text("text1");
        proxy.text("text2");
        proxy.text("text3");
        proxy.endTemplate(new WikiTextParserHandler.TemplateName("tt1"));
        proxy.beginTemplate(new WikiTextParserHandler.TemplateName("tt2"));
        proxy.parameter("pp11");
        proxy.text("text pp11");
        proxy.endTemplate(new WikiTextParserHandler.TemplateName("tt2"));
        proxy.parameter("p3");
        proxy.text("text4");
        proxy.endTemplate(new WikiTextParserHandler.TemplateName("t1"));
        proxy.endDocument();

        Assert.assertEquals(0, splitter.getActiveRedirections().size());
        Assert.assertEquals(1, splitter.getcompletedRedirections().size());
        Assert.assertNotNull(splitter.getcompletedRedirections().get(REDIRECTION_NAME));

        final WikiTextHRDumperHandler out = new WikiTextHRDumperHandler();
        buffer.flush(out);
        Assert.assertEquals(
            "Begin Template: tt1\n" +
            "k: pp1\n" +
            "Text: 'text1'\n" +
            "Text: 'text2'\n" +
            "Text: 'text3'\n" +
            "End Template: tt1\n",

             out.getContent()
        );
    }

}
