package com.machinelinking.parser;

/**
 * Any key/value attribute parsed by {@link com.machinelinking.parser.AttributeScanner}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Attribute {

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