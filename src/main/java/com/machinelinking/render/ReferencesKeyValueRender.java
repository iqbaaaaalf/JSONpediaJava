package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ReferencesKeyValueRender implements KeyValueRender {

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException {
        final Iterator<JsonNode> links = value.iterator();
        JsonNode link;
        writer.openTag("div");
        writer.heading(1, "References");
        while(links.hasNext()) {
            link = links.next();
            final String label = link.get("label").asText();
            final String[] labelParts = getLinkLabel(label);
            String description = link.get("description").asText();
            if(description.trim().length() == 0) {
                description = labelParts[1].replaceAll("_", " ");
            }
            writer.reference(description, labelParts[0], labelParts[1]);
        }
        writer.closeTag();
    }

    private String[] getLinkLabel(String in) {
        final String HTTP_PREFIX = "http://";
        if(in.startsWith(HTTP_PREFIX)) {
            return new String[] {
                    in.substring(HTTP_PREFIX.length(), in.indexOf('.')),
                    in.substring(in.lastIndexOf('/') + 1)
            };
        } else {
            return in.split(":");
        }
    }

}
