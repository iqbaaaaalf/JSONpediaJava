package com.machinelinking.render;

import com.machinelinking.util.JsonPathBuilder;
import org.codehaus.jackson.JsonNode;

/**
 * Maintains the current context of a {@link org.codehaus.jackson.JsonNode} visit.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JsonContext {

    DocumentContext getDocumentContext();

    String getDocumentTitle();

    String getLang();

    String getDomain();

    String getJSONPath();

    JsonNode getRoot();

    boolean subPathOf(JsonPathBuilder builder, boolean strict);

}
