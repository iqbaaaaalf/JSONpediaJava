package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class IssuesKeyValueRender implements KeyValueRender {

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer) throws IOException {
        writer.openTag("pre");
        if(value.isNull()) {
            writer.image("/frontend/img/ok.png", "OK");
            writer.text("No issues detected");
        } else {
            writer.image("/frontend/img/warn.png", "Error");
            writer.text(value.asText());
        }
        writer.closeTag();
    }

}
