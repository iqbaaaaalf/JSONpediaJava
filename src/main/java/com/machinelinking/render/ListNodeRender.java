package com.machinelinking.render;

import com.machinelinking.pagestruct.PageStructConsts;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ListNodeRender implements NodeRender {

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer) throws IOException {
        final JsonNode content = node.get(PageStructConsts.CONTENT_FIELD);
        if(content == null || content.isNull()) return;
        writer.openList();
        for(JsonNode item : content) {
            writer.openListItem();
            rootRender.render(context, rootRender, item.get(PageStructConsts.CONTENT_FIELD), writer);
            writer.closeListItem();
        }
        writer.closeList();
    }
}
