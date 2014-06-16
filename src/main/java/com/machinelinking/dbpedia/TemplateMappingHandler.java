package com.machinelinking.dbpedia;

import com.machinelinking.parser.DefaultWikiTextParserHandler;

/**
 * Specific {@link com.machinelinking.parser.WikiTextParserHandler}
 * to parse <i>Template Mapping</i>s from <i>DBpedia</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class TemplateMappingHandler extends DefaultWikiTextParserHandler {

    public static final String TEMPLATE_MAPPING_NAME = "TemplateMapping";
    public static final String PROPERTY_MAPPING_NAME = "PropertyMapping";
    public static final String CLASS_PROPERTY_NAME    = "mapToClass";
    public static final String TEMPLATE_PROPERTY_NAME = "templateProperty";
    public static final String ONTOLOGY_PROPERTY_NAME = "ontologyProperty";

    private final String mappingName;

    private boolean insideTemplateMapping = false;
    private boolean insidePropertyMapping = false;

    private boolean nextIsTemplateClass    = false;
    private boolean nextIsTemplateProperty = false;
    private boolean nextIsOntologyProperty = false;
    private String templateProperty;
    private String ontologyProperty;

    private String clazz;

    private TemplateMapping mapping;

    protected TemplateMappingHandler(String mappingName) {
        this.mappingName = mappingName;
    }

    public abstract void handle(TemplateMapping mapping);

    @Override
    public void beginTemplate(TemplateName name) {
        final String n = name.plain.trim();
        if(TEMPLATE_MAPPING_NAME.equalsIgnoreCase(n)) {
            insideTemplateMapping = true;
            nextIsTemplateClass = false;
            if(mapping != null) throw new IllegalArgumentException("Unsupported conditional mapping.");
        } else if(insideTemplateMapping && PROPERTY_MAPPING_NAME.equalsIgnoreCase(n.trim())) {
            insidePropertyMapping = true;
        }
    }

    @Override
    public void parameter(String param) {
        param = param.trim();
        if (CLASS_PROPERTY_NAME.equals(param)) {
            nextIsTemplateClass = true;
        }
        if(!insidePropertyMapping) return;
        nextIsTemplateProperty = nextIsOntologyProperty = false;
        if(TEMPLATE_PROPERTY_NAME.equalsIgnoreCase(param)) {
            nextIsTemplateProperty = true;
            templateProperty = ontologyProperty = null;
        } else if (ONTOLOGY_PROPERTY_NAME.equalsIgnoreCase(param)) {
            nextIsOntologyProperty = true;
        }
    }

    @Override
    public void text(String content) {
        if (nextIsTemplateClass) {
            clazz = content.trim();
            nextIsTemplateClass = false;
            return;
        }
        if(!insidePropertyMapping) return;
        if(nextIsOntologyProperty) {
            ontologyProperty = content;
        } else if(nextIsTemplateProperty) {
            templateProperty = content;
        }
        if(templateProperty != null && ontologyProperty != null) {
            if(mapping == null) {
                mapping = new TemplateMapping(mappingName, clazz);
            }
            mapping.addMapping(templateProperty.trim(), ontologyProperty.trim());
            templateProperty = ontologyProperty = null;
        }
    }

    @Override
    public void endTemplate(TemplateName name) {
        final String n = name.plain.trim();
        if(insidePropertyMapping && PROPERTY_MAPPING_NAME.equalsIgnoreCase(n)) {
            insidePropertyMapping = false;
        } else if(insideTemplateMapping && TEMPLATE_MAPPING_NAME.equalsIgnoreCase(n)) {
            insideTemplateMapping = false;
            handle(mapping);
        }
    }

}
