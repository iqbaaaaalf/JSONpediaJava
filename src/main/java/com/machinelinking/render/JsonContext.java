package com.machinelinking.render;

import com.machinelinking.util.JsonPathBuilder;

/**
 * Maintains the current context of a {@link org.codehaus.jackson.JsonNode} visit.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JsonContext {

    String getJSONPath();

    boolean subPathOf(JsonPathBuilder builder, boolean strict);

}
