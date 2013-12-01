package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SectionsKeyValueRender implements KeyValueRender {

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException {
        final StringBuilder sb = new StringBuilder();
        final Iterator<JsonNode> sections = value.getElements();
        JsonNode section;
        sb.append("<pre>");
        while (sections.hasNext()) {
            section = sections.next();
            toTabulation(section.get("level").asInt(), sb);
            final String title = section.get("title").asText();
            sb.append(String.format("<a href=\"#%s\">", SectionRender.toAnchorName(title)));
            sb.append(title);
            sb.append("</a>");
            sb.append("\n");
        }
        sb.append("</pre>");

        writer.openTag("div");
        writer.heading(1, "Sections");
        writer.html(sb.toString());
        writer.closeTag();
    }

    private void toTabulation(int t, StringBuilder sb) {
        for(int i = 0; i < t; i++) {
            sb.append("\t");
        }
    }

}
