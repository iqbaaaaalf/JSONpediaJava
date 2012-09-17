package com.machinelinking.pagestruct;

import com.machinelinking.parser.DefaultWikiTextParserHandler;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class WikiMappingHandler extends DefaultWikiTextParserHandler {

    public static final String TEMPLATE_MAPPING_NAME = "TemplateMapping";
    public static final String PROPERTY_MAPPING_NAME = "PropertyMapping";
    public static final String TEMPLATE_PROPERTY_NAME = "templateProperty";
    public static final String ONTOLOGY_PROPERTY_NAME = "ontologyProperty";

    private boolean insideTemplateMapping = false;
    private boolean insidePropertyMapping = false;

    private boolean nextIsTemplateProperty = false;
    private boolean nextIsOntologyProperty = false;
    private String templateProperty;
    private String ontologyProperty;

    private TemplateMapping mapping;

    public abstract void handle(TemplateMapping mapping);

    @Override
    public void beginTemplate(String name) {
        if(TEMPLATE_MAPPING_NAME.equalsIgnoreCase(name.trim())) {
            insideTemplateMapping = true;
            if(mapping != null) throw new IllegalArgumentException("Unsupported nested mapping.");
            mapping = new TemplateMapping(name);
        } else if(insideTemplateMapping && PROPERTY_MAPPING_NAME.equalsIgnoreCase(name.trim())) {
            insidePropertyMapping = true;
        }
    }

    @Override
    public void parameter(String param) {
        if(!insidePropertyMapping) return;
        nextIsTemplateProperty = nextIsOntologyProperty = false;
        if(TEMPLATE_PROPERTY_NAME.equalsIgnoreCase(param.trim())) {
            nextIsTemplateProperty = true;
            templateProperty = ontologyProperty = null;
        } else if (ONTOLOGY_PROPERTY_NAME.equalsIgnoreCase(param.trim())) {
            nextIsOntologyProperty = true;
        }
    }

    @Override
    public void text(String content) {
        if(!insidePropertyMapping) return;
        if(nextIsOntologyProperty) {
            ontologyProperty = content;
        } else if(nextIsTemplateProperty) {
            templateProperty = content;
        }
        if(templateProperty != null && ontologyProperty != null) {
            mapping.addMapping(templateProperty.trim(), ontologyProperty.trim());
            templateProperty = ontologyProperty = null;
        }
    }

    @Override
    public void endTemplate(String name) {
        if(insidePropertyMapping && PROPERTY_MAPPING_NAME.equalsIgnoreCase(name.trim())) {
            insidePropertyMapping = false;
        } else if(insideTemplateMapping && TEMPLATE_MAPPING_NAME.equalsIgnoreCase(name.trim())) {
            insideTemplateMapping = false;
            handle(mapping);
        }
    }

}
