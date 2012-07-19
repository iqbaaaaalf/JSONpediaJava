package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface CriteriaNodeRender extends NodeRender {

    boolean acceptNode(JsonNode node);


}
