package com.machinelinking.extractor;

import com.machinelinking.parser.FilteredHandlerCriteria;
import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.parser.WikiTextParserFilteredHandler;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Test case for {@link com.machinelinking.extractor.TextExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TextExtractorTest {

    @Test
    public void testTextExtraction() throws IOException, WikiTextParserException {
        final TextExtractor extractor = new TextExtractor();
        //TODO: move inside TextExtractor logic.
        final WikiTextParser parser = new WikiTextParser(
                new WikiTextParserFilteredHandler(
                        extractor,
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

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer serializer = new JSONSerializer(baos);
        serializer.openList();
        extractor.flushContent(serializer);
        serializer.closeList();
        serializer.flush();

        Assert.assertEquals(
                "Invalid text extraction",
                JSONUtils.parseJSON(
                        IOUtils.toString(this.getClass().getResourceAsStream("Page1-textextractor.json"))
                ),
                JSONUtils.parseJSON(baos.toString())
        );
    }

}
