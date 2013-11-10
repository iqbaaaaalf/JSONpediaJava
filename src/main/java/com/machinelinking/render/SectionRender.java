package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SectionRender implements NodeRender {

    private static final Map<String,String> SECTION_DIV_ATTR = new HashMap<String,String>(){{
        put("class", "section");
    }};

    public static final String TITLE = "title";
    public static final String LEVEL = "level";

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws IOException {
        final String title = node.get(TITLE).asText();
        int level = node.get(LEVEL).asInt() + 1;
        writer.openTag("div", SECTION_DIV_ATTR);
        writer.heading(level, title);
        writer.closeTag();
    }

}
