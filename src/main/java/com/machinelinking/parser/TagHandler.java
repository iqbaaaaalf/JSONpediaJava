package com.machinelinking.parser;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TagHandler {

    void beginTag(String node, Attribute[] attributes);

    void endTag(String node);

    void inlineTag(String node, Attribute[] attributes);

    void commentTag(String comment);

    class Attribute {
        public final String name;
        public final String value;
        public Attribute(String name, String value) {
            this.name = name;
            this.value = value;
        }
        @Override
        public String toString() {
            return String.format("%s : '%s'", name, value);
        }
    }

}
