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

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplatesMappingKeyValueRender implements KeyValueRender {

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException {
        final Iterator<JsonNode> mappingCollections = value.get("mapping-collection").getElements();
        JsonNode mappingCollection;
        writer.openTag("div");
        writer.heading(1, "Mapping Collections");
        int mappingIndex = 0;
        while(mappingCollections.hasNext()) {
            mappingCollection = mappingCollections.next();
            processMappingCollection(mappingIndex++, context.getRoot(), mappingCollection, writer);
        }
        writer.closeTag();
    }

    private void processMappingCollection(int mappingIndex, JsonNode root, JsonNode mappingCollection, HTMLWriter writer)
    throws IOException {
        final String mappingCollectionName = mappingCollection.get("name").asText();
        final String mappingCollectionClass = mappingCollection.get("class").asText();
        final Iterator<Map.Entry<String,JsonNode>> mappings = mappingCollection.get("mapping").getFields();
        Map.Entry<String,JsonNode> mapping;
        writer.openTag("div");
        writer.openTag("div");
        writer.heading(2, String.format("%s (%s)", mappingCollectionName, mappingCollectionClass));
        writer.closeTag();
        final JsonNode infobox = root.get("infobox-splitter");
        final JsonNode infoboxContent = infobox == null ? null : infobox.get(mappingIndex).get("content");
        while(mappings.hasNext()) {
            mapping = mappings.next();
            final String field = mapping.getKey();
            final String property = asText(mapping.getValue().get("name"));
            final String label = asText(mapping.getValue().get("label"));
            final String domain = asText(mapping.getValue().get("domain"));
            final String range = asText(mapping.getValue().get("range"));
            final String value = getInfoboxProperty(infoboxContent, field);
            writer.openTag("div");
            writer.html(
                    String.format(
                            "%s :: %s -- %s (%s) --> %s %s",
                            field, domain, property, label, range,
                            value == null
                                ?
                                    "[?]"
                                :
                                    String.format(
                                            "<div class=\"mapping-accordion\">\n" +
                                            "   <h5>[value+]</h5>\n" +
                                            "   <div>\n" +
                                            "       <pre class=\"mapping-value\">%s</pre>\n" +
                                            "   </div>" +
                                            "</div>",
                                            value
                                    )
                    )
            );
            writer.closeTag();
        }
        writer.closeTag();
    }

    private String asText(JsonNode node) {
        return node == null ? "" : node.asText();
    }

    private String getInfoboxProperty(JsonNode infoboxContent, String field) {
        if(infoboxContent == null) return null;
        final JsonNode fieldJson = infoboxContent.get(field);
        return JSONUtils.serializeToJSON(fieldJson, true);
    }

}
