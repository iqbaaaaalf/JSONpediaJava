package com.machinelinking.filter;

import org.codehaus.jackson.JsonNode;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONFilterEngine {

    JsonNode[] filter(JsonNode in, JSONFilter filter);

}
