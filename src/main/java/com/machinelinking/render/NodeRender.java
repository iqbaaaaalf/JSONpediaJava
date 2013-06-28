package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;


/**
 * Defines the renderer for a {@link JsonNode}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface NodeRender {

    boolean acceptNode(JsonContext context, JsonNode node);

    void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws IOException;

}
