package com.machinelinking.render;

import com.machinelinking.util.JSONUtils;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Test case for {@link DefaultHTMLRender}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultHTMLRenderTest {

    @Test
    public void testRender() throws IOException {
        final JsonNode node = JSONUtils.parseJSON(
                this.getClass().getResourceAsStream(
                        "Enrichment1.json"
                )
        );
        final URL documentURL = new URL("http://en.wikipedia.org/page/Fake");
        final String html = DefaultHTMLRenderFactory.getInstance().createRender().renderToHTML(documentURL, node);
        FileUtils.writeStringToFile( new File("./test-render.html"), html);
    }

}
