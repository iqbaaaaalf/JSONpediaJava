package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface HTMLRenderFactory {

    HTMLRender createRender();

    String renderToHTML(JsonNode rootNode) throws IOException;

}
