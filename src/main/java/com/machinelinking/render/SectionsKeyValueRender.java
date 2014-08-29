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
    throws NodeRenderException {
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

        try {
            writer.openTag("div");
            writer.heading(1, "Sections");
            writer.html(sb.toString());
            writer.closeTag();
        } catch (IOException ioe) {
            throw new NodeRenderException(ioe);
        }
    }

    private void toTabulation(int t, StringBuilder sb) {
        for(int i = 0; i < t; i++) {
            sb.append("\t");
        }
    }

}
