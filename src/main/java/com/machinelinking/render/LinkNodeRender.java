package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class LinkNodeRender implements NodeRender {

    @Override
    public void render(RootRender rootRender, JsonNode node, HTMLWriter writer) throws IOException {
        final String url = node.get("url").asText();
        final String description = node.get("description").asText().trim();
        writer.openTag("a", new HashMap<String, String>() {{
            put("href", url);
        }});
        writer.text(description);
        writer.closeTag();
    }

}
