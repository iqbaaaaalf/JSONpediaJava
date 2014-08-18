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
