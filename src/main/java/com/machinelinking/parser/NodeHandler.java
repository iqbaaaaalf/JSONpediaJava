package com.machinelinking.parser;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface NodeHandler {

    void startElement(String name);

    void endElement(String name);

}
