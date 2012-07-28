package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ContentKeyValueRender implements KeyValueRender {

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException {
        if(value.isNull()) {
            writer.openTag("i");
            writer.text("&lt;null&gt;");
            writer.closeTag();
        } else if(( value.isArray() || value.isObject() ) && value.size() == 0) {
            writer.openTag("i");
            writer.text("&lt;empty&gt;");
            writer.closeTag();
        } else {
            rootRender.render(context, rootRender, "Content", value, writer);
        }
    }

}
