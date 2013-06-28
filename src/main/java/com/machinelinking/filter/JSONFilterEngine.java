package com.machinelinking.filter;

import org.codehaus.jackson.JsonNode;

/**
 * Defines an engine able to apply {@link JSONFilter}s over {@link JsonNode}s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONFilterEngine {

    JsonNode[] filter(JsonNode in, JSONFilter filter);

}
