package com.machinelinking.dbpedia;

/**
 * Defines a <i>DBpedia</i> property.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Property {

    public String getPropertyName();

    public String getPropertyLabel();

    public String getPropertyDomain();

    public String getPropertyRange();

}
