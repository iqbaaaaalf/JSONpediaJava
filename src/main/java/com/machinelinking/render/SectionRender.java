package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SectionRender implements NodeRender {

    public static final String TITLE = "title";
    public static final String LEVEL = "level";

    @Override
    public void render(RootRender rootRender, JsonNode node, HTMLWriter writer) throws IOException {
        final String title = node.get(TITLE).asText();
        int level    = node.get(LEVEL).asInt() + 1;
        if(level > 6) level = 6;
        writer.text( String.format("<font color=\"green\"><h%d>%s</h%d></font>",level, title, level) );
    }

}
