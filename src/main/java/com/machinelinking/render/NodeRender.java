package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;


/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface NodeRender {

    void render(RootRender rootRender, JsonNode node, HTMLWriter writer) throws IOException;

}
