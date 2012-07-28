package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class LinkNodeRender implements NodeRender {

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws IOException {
        final String url = node.get("url").asText();
        final String description = node.get("description").asText().trim();
        writer.openTag("a", new HashMap<String, String>() {{
            put("href", url);
            put("style", "background-color: #C95095");
        }});
        writer.text(description);
        writer.closeTag();
    }

}
