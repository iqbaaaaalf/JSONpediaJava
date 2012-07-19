package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface KeyValueRender {

    void render(RootRender rootRender, String key, JsonNode value, HTMLWriter writer) throws IOException;

}
