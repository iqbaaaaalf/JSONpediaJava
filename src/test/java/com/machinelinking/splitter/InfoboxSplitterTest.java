package com.machinelinking.splitter;

import com.machinelinking.parser.MultiWikiTextParserHandler;
import com.machinelinking.serializer.JSONSerializer;
import junit.framework.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Test case for {@link InfoboxSplitter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InfoboxSplitterTest {

    @Test
    public void testSplitter() throws IOException {

        // TODO: there should be a class incapsulating the logic of this code block used by WikiEnricher.
        final InfoboxSplitter splitter = new InfoboxSplitter();
        final WikiTextParserHandlerSplitter splitterHandler = new WikiTextParserHandlerSplitter();
        splitter.initHandlerSplitter(splitterHandler);
        final MultiWikiTextParserHandler multiHandler = new MultiWikiTextParserHandler();
        multiHandler.add(splitterHandler.getProxy());
        multiHandler.add(splitter);

        multiHandler.beginDocument(new URL("http://test/doc"));
        multiHandler.beginTemplate("t1");
        multiHandler.parameter("t1-p1");
        multiHandler.beginTemplate("tt1");
        splitter.split(); // < Split here
        multiHandler.parameter("tt1-p1");
        multiHandler.text("text 1");
        multiHandler.text("text 2");
        multiHandler.endTemplate("tt1");
        multiHandler.parameter("t1-p2");
        multiHandler.text("text 3");
        multiHandler.endTemplate("t1");
        multiHandler.endDocument();

        Assert.assertEquals(0, splitterHandler.getActiveRedirections().size() );
        final Map<String,WikiTextParserHandlerSplitter.Redirect> redirections =
                splitterHandler.getcompletedRedirections();
        Assert.assertEquals(1, redirections.size());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer serializer = new JSONSerializer(baos);
        serializer.openObject();
        splitter.serialize(serializer);
        serializer.closeObject();
        serializer.flush();
        System.out.println(baos);
    }

}
