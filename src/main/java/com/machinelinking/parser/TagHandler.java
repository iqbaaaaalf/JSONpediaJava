package com.machinelinking.parser;

/**
 * Defines the <i>HTML</i> markup handler for the {@link WikiTextParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TagHandler {

    void beginTag(String node, Attribute[] attributes);

    void endTag(String node);

    void inlineTag(String node, Attribute[] attributes);

    void commentTag(String comment);

}
