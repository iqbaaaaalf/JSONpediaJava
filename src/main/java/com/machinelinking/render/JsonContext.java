package com.machinelinking.render;

import com.machinelinking.util.JsonPathBuilder;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JsonContext {

    String getJSONPath();

    boolean subPathOf(JsonPathBuilder builder);

}
