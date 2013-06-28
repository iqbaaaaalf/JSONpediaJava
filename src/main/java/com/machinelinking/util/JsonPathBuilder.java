package com.machinelinking.util;

/**
 * Builder for generating a <i>JSON Path</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JsonPathBuilder {

    void startPath();

    void enterArray();

    void arrayElem();

    void exitArray();

    void enterObject();

    void field(String fieldName);

    void exitObject();

    String getJsonPath();

    boolean subPathOf(JsonPathBuilder other, boolean strict);

}
