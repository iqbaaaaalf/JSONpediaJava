package com.machinelinking.render;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TitleKeyValueRender implements KeyValueRender {

    private static final Map<String,String> TITLE_DIV_ATTR = new HashMap<String,String>(){{
        put("style", "background-color: #8A5D37");
    }};

    @Override
    public void render(RootRender rootRender, String key, JsonNode value, HTMLWriter writer) throws IOException {
        final String valueStr = JSONUtils.asPrimitiveString(value);
        writer.openTag("strong", TITLE_DIV_ATTR);
        //writer.openColorTag("purple");
        writer.text(valueStr);
        //writer.closeTag();
        writer.closeTag();
    }
}
