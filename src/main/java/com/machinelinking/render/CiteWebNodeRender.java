package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CiteWebNodeRender implements NodeRender {

    public static final String CITE_WEB_TEMPLATE_NAME = "cite web";

    public static final String[] TEMPLATE_FIELDS = new String[] {
            "title", "publisher", "author", "last", "work", "archivedate", "accessdate", "url", "archiveurl",
    };

    @Override
    public boolean acceptNode(JsonNode node) {
        final JsonNode name = node.get(TemplateConstants.TEMPLATE_NAME);
        return name != null && CITE_WEB_TEMPLATE_NAME.equals(name.asText());
    }

    @Override
    public void render(RootRender rootRender, JsonNode node, HTMLWriter writer) throws IOException {
        writer.openTable("Cite Web");
        JsonNode value;
        final JsonNode content = node.get(TemplateConstants.TEMPLATE_CONTENT);
        for(String field: TEMPLATE_FIELDS) {
            value = content.get(field);
            if(value == null) continue;
            writer.openTag("tr");
            writer.openTag("td");
            rootRender.render(rootRender, field, value, writer); // TODO: add check that a sub node is taken to prevent loop.
            writer.closeTag();
            writer.closeTag();
        }
        writer.closeTable();
    }

}
