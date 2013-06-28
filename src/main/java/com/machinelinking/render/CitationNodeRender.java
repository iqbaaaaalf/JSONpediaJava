package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <i>Citation</i> template render.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CitationNodeRender implements NodeRender {

    public static final String CITATION_TEMPLATE_NAME = "Citation";

    private static final Map<String,String> CITATION_DIV_ATTR = new HashMap<String,String>(){{
        put("style", "background-color: #B86725");
    }};

    private static final String[] TEMPLATE_FIELDS = {
            "content",
            "author",
            "year",
            "publisher",
            "location",
            "pages",
            "name",
            "url",
            "accessdate",
            "isbn",
            "first",
            "last",
            "contribution",
            "format",
            "postscript",
    };

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        final JsonNode name = node.get(TemplateConstants.TEMPLATE_NAME);
        return name != null && CITATION_TEMPLATE_NAME.equals(name.asText());
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws IOException {
        final JsonNode content = node.get(TemplateConstants.TEMPLATE_CONTENT);
        writer.openTag("div", CITATION_DIV_ATTR);
        writer.openTag("strong");
        writer.text("Citation: ");
        writer.closeTag();
        JsonNode value;
        for(String field : TEMPLATE_FIELDS) {
            value = content.get(field);
            if(value == null) continue;
            rootRender.render(context, rootRender, field, value, writer);
        }
        writer.closeTag();
    }

}
