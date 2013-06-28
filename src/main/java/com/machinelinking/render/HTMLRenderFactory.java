package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * Factory for an {@link HTMLRender}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface HTMLRenderFactory {

    HTMLRender createRender();

    String renderToHTML(JsonNode rootNode) throws IOException;

}
