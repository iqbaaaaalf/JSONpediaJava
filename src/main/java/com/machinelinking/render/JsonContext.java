package com.machinelinking.render;

import com.machinelinking.util.JsonPathBuilder;

import java.net.URL;

/**
 * Maintains the current context of a {@link org.codehaus.jackson.JsonNode} visit.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JsonContext {

    URL getDocumentURL();

    String getJSONPath();

    boolean subPathOf(JsonPathBuilder builder, boolean strict);

}
