package com.machinelinking.dbpedia;


import java.io.Serializable;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultPropertyMapping implements PropertyMapping, Serializable {

    private final String property;

    private final String label;

    private final String domain;

    private final String range;

    public DefaultPropertyMapping(String property, String label, String domain, String range) {
        if(property == null) throw new NullPointerException();
        this.property = property;
        this.label = label;
        this.domain = domain;
        this.range = range;
    }

    @Override
    public String getPropertyName() {
        return property;
    }

    @Override
    public String getPropertyLabel() {
        return label;
    }

    @Override
    public String getPropertyDomain() {
        return domain;
    }

    @Override
    public String getPropertyRange() {
        return range;
    }

    @Override
    public String toString() {
        return String.format("{property=%s label='%s' domain=%s range=%s}", property, label, domain, range);
    }
}
