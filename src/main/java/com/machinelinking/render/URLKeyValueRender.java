package com.machinelinking.render;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class URLKeyValueRender implements KeyValueRender {

    private static final Map<String,String> URLKEY_DIV_ATTR = new HashMap<String,String>(){{
        put("style", "background-color: #6F2C52");
    }};

    @Override
    public void render(RootRender rootRender, String key, JsonNode value, HTMLWriter writer) throws IOException {
        final String valueStr = JSONUtils.asPrimitiveString(value);
        writer.openTag("div", URLKEY_DIV_ATTR);
        writer.key(key);
        writer.anchor(valueStr, valueStr);
        writer.closeTag();
    }

}
