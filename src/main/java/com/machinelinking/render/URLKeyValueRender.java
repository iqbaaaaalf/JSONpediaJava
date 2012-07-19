package com.machinelinking.render;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class URLKeyValueRender implements KeyValueRender {

    @Override
    public void render(RootRender rootRender, String key, JsonNode value, HTMLWriter writer) throws IOException {
        final String valueStr = JSONUtils.asPrimitiveString(value);
        writer.key(key);
        writer.anchor(valueStr, valueStr);
    }

}
