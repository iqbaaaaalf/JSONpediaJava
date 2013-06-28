package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * Defines the render for a <i>key/value</i> pair.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface KeyValueRender {

    void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException;

}
