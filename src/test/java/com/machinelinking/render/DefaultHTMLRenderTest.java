package com.machinelinking.render;

import com.machinelinking.util.JSONUtils;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultHTMLRenderTest {

    @Test
    public void testRender() throws IOException {
        final JsonNode node = JSONUtils.parseJSON(
                this.getClass().getResourceAsStream(
                        "/Enrichment1.json"
                )
        );
        final String html = DefaultHTMLRenderFactory.getInstance().renderToHTML(node);
        FileUtils.writeStringToFile( new File("./test-render.html"), html);
    }

}
