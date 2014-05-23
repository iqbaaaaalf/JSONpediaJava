package com.machinelinking.render;

import com.machinelinking.wikimedia.WikimediaUtils;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CategoriesKeyValueRender implements KeyValueRender {

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException {
        final Iterator<JsonNode> categories = value.get("content").getElements();
        writer.openTag("div");
        writer.heading(1, "Categories");
        final String lang = WikimediaUtils.urlToParts(context.getDocumentContext().getDocumentURL()).lang;
        while(categories.hasNext()) {
            writer.category(lang, categories.next().asText());
        }
        writer.closeTag();
    }

}
