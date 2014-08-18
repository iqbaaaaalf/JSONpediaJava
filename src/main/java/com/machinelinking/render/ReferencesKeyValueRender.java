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

import com.machinelinking.extractor.Reference;
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
            final String url = link.get("url").asText();
            final String[] labelParts = Reference.urlToLabel(url);
            String description = link.get("description").asText();
            if(description.trim().length() == 0) {
                description = labelParts[1].replaceAll("_", " ");
            }
            writer.reference(description, labelParts[0], labelParts[1], true);
        }
        writer.closeTag();
    }

}
