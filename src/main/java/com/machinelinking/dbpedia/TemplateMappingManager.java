package com.machinelinking.dbpedia;

/**
 * Defines a manager to load collections of {@link com.machinelinking.dbpedia.TemplateMapping}s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TemplateMappingManager {

    String[] getMappingNames();

    TemplateMapping getMapping(String templateName);

    int getMappingsCount();

}
