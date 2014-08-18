/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

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

    private String mappingName;

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
       reset(mappingName);
    }

    public abstract void handle(TemplateMapping mapping);

    public void reset(String mappingName) {
        this.mappingName = mappingName;
        insideTemplateMapping = false;
        insidePropertyMapping = false;
        nextIsTemplateClass    = false;
        nextIsTemplateProperty = false;
        nextIsOntologyProperty = false;
        templateProperty = null;
        ontologyProperty = null;
        clazz = null;
        mapping = null;
    }

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
        param = param == null ? null : param.trim();
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
                mapping = TemplateMappingFactory.getInstance().createMapping(mappingName, clazz);
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
            if(mapping != null)handle(mapping);
        }
    }

}
