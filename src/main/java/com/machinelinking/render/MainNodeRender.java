package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MainNodeRender implements NodeRender {

    public static final String MAIN_TEMPLATE_NAME = "Main";

    private static final Map<String,String> MAIN_DIV_ATTR = new HashMap<String,String>(){{
        put("style", "background-color: purple");
    }};

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        final JsonNode name = node.get(TemplateConstants.TEMPLATE_NAME);
        return name != null && MAIN_TEMPLATE_NAME.equalsIgnoreCase(name.asText());
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws IOException {
        writer.openTag("div", MAIN_DIV_ATTR);
        writer.openTag("strong");
        writer.text("Main Article: ");
        writer.closeTag();
        final JsonNode content = node.get(TemplateConstants.TEMPLATE_CONTENT);
        final Iterator<String> articles = content.getFieldNames();
        String article;
        while(articles.hasNext()) {
            article = articles.next();
            writer.anchor( String.format("http://en.wikipedia.org/wiki/%s", article), article, true ); // TODO: en. must be parametric.
            writer.text(" ");
        }
        writer.closeTag();
    }

}
