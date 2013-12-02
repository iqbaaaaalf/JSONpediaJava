package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class LinksKeyValueRender implements KeyValueRender {

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException {
        final Iterator<JsonNode> links = value.iterator();
        JsonNode link;
        writer.openTag("div");
        writer.heading(1, "Links");
        while(links.hasNext()) {
            link = links.next();
            writer.link(
                    link.get("description").asText(),
                    link.get("url").asText()
            );
        }
        writer.closeTag();
    }

}
