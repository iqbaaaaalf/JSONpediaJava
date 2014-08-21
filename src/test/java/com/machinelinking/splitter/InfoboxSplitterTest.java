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

import com.machinelinking.parser.MultiWikiTextParserHandler;
import com.machinelinking.parser.WikiTextParserHandler;
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
        multiHandler.beginTemplate(new WikiTextParserHandler.TemplateName("t1"));
        multiHandler.parameter("t1-p1");
        multiHandler.beginTemplate(new WikiTextParserHandler.TemplateName("tt1"));
        splitter.split(); // < Split here
        multiHandler.parameter("tt1-p1");
        multiHandler.text("text 1");
        multiHandler.text("text 2");
        multiHandler.endTemplate(new WikiTextParserHandler.TemplateName("tt1"));
        multiHandler.parameter("t1-p2");
        multiHandler.text("text 3");
        multiHandler.endTemplate(new WikiTextParserHandler.TemplateName("t1"));
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
        Assert.assertEquals(
            "{\"infobox-splitter\":[{\"@type\":\"template\",\"name\":\"tt1\"," +
            "\"content\":{\"tt1-p1\":[\"text 1\",\"text 2\"]}}]}",
            baos.toString()
        );
    }

}
