package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FreebaseKeyValueRender implements KeyValueRender {

    private static final Map<String,String> TYPE_DIV_ATTR = new HashMap<String,String>(){{
        put("style", "padding: 5px");
    }};

    @Override
    public void render(RootRender rootRender, String key, JsonNode value, HTMLWriter writer) throws IOException {
        writer.openTag("div");

        writer.heading(3, "Freebase");

        final JsonNode aliases = value.get("alias");
        if(aliases.size() > 0) {
            writer.openTag("pre");
            for(int i = 0; i < aliases.size(); i++) {
                writer.text(aliases.get(i).asText());
            }
            writer.closeTag();
        }


        final JsonNode guid = value.get("guid");
        writer.openTag("pre");
        writer.text(guid.asText());
        writer.closeTag();

        final JsonNode id   = value.get("id");
        final JsonNode name = value.get("name");
        writer.anchor( String.format("http://www.freebase.com/view/%s", id.asText()), name.asText() );

        writer.heading(4, "Types");
        final JsonNode types = value.get("type");
        JsonNode elem;
        for(int i=0; i < types.size(); i++) {
            elem = types.get(i);
            writer.openTag("span", TYPE_DIV_ATTR);
            writer.anchor(
                    String.format("http://www.freebase.com/view/%s", elem.get("id").asText()),
                    elem.get("name").asText()
            );
            writer.closeTag();
        }

        writer.closeTag();
    }
}
