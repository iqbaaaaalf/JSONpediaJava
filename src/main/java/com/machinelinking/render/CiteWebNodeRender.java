package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <i>Cite Web</i> template render.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CiteWebNodeRender implements NodeRender {

    public static final String CITE_WEB_TEMPLATE_NAME = "cite web";

    private static final Map<String,String> CITEWEB_DIV_ATTR = new HashMap<String,String>(){{
        put("class", "cite-web");
    }};

    public static final String[] TEMPLATE_FIELDS = new String[] {
            "title", "publisher", "author", "last", "work", "archivedate", "accessdate", "url", "archiveurl",
    };

    @Override
    public boolean acceptNode(JsonContext context,  JsonNode node) {
        final JsonNode name = node.get(TemplateConstants.TEMPLATE_NAME);
        return name != null && CITE_WEB_TEMPLATE_NAME.equals(name.asText());
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws IOException {
        writer.openTag("div", CITEWEB_DIV_ATTR);
        writer.openTable("Cite Web", null);
        JsonNode value;
        final JsonNode content = node.get(TemplateConstants.TEMPLATE_CONTENT);
        for(String field: TEMPLATE_FIELDS) {
            value = content.get(field);
            if(value == null) continue;
            writer.openTag("tr");
            writer.openTag("td");
            rootRender.render(context, rootRender, field, value, writer);
            writer.closeTag();
            writer.closeTag();
        }
        writer.closeTable();
        writer.closeTag();
    }

}
