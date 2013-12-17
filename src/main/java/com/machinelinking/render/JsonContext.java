package com.machinelinking.render;

import com.machinelinking.util.JsonPathBuilder;
import org.codehaus.jackson.JsonNode;

import java.net.URL;

/**
 * Maintains the current context of a {@link org.codehaus.jackson.JsonNode} visit.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JsonContext {

    URL getDocumentURL();

    String getJSONPath();

    JsonNode getRoot();

    boolean subPathOf(JsonPathBuilder builder, boolean strict);

}
