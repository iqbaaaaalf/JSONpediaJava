package com.machinelinking.extractor;

import com.machinelinking.parser.FilteredHandlerCriteria;
import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.parser.WikiTextParserFilteredHandler;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * Test case for {@link TextHandler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TextHandlerTest {

    @Test
    public void testAbstractTextExtraction() throws IOException, WikiTextParserException {
        final TextHandler textHandler = new TextHandler();
        final WikiTextParser parser = new WikiTextParser(
                new WikiTextParserFilteredHandler(
                        textHandler,
                        new FilteredHandlerCriteria() {
                            @Override
                            public boolean mustFilter(int paragraphIndex, int sectionLevel, int nestingLevel) {
                                return paragraphIndex == 0 || sectionLevel != -1;
                            }
                        }));
        parser.parse(
                new URL("http://test/page1"),
                this.getClass().getResourceAsStream("Page1.wikitext")
        );


        Assert.assertEquals(
                "Invalid text extraction",
                IOUtils.toString(this.getClass().getResourceAsStream("Page1-texthandler.txt")),
                textHandler.flushContent()
        );
    }

}