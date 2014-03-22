package com.machinelinking.converter;

import com.machinelinking.util.JSONUtils;

/**
 * Set of functions added to the {@link com.machinelinking.converter.ScriptableConverter} global scope.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ScriptContextFunctions {

    public String as_text(Object in) {
        return JSONUtils.toHumanReadable(in);
    }

}
