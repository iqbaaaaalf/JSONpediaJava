package com.machinelinking.dbpedia;

import com.machinelinking.pagestruct.PageStructConsts;
import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines a mapping between a <i>Wikipedia Template</i> and <i>DBpedia</i> ontology.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateMapping implements Serializable, java.io.Serializable {

    private transient OntologyManager ontologyManager;
    private final String mappingName;
    private final String mappingClass;
    private final Map<String,Property> propertyNameToPropertyMapping;

    private List<String> issues;

    public TemplateMapping(String mappingName, String mappingClass) {
        this.mappingName = mappingName;
        this.mappingClass = mappingClass;
        this.propertyNameToPropertyMapping = new HashMap<>();
    }

    public String getMappingName() {
        return mappingName;
    }

    public String getMappingClass() {
        return mappingClass;
    }

    public Property getMappingForProperty(String property) {
        return propertyNameToPropertyMapping.get(property);
    }

    public int getMappingSize() {
        return propertyNameToPropertyMapping.size();
    }

    public void serialize(Serializer serializer) {
        serializer.openObject();
        serializer.fieldValue(PageStructConsts.TYPE_FIELD, PageStructConsts.TYPE_MAPPING);
        serializer.fieldValue("name", getMappingName());
        serializer.fieldValue("class", getMappingClass());

        serializer.field("mapping");
        serializer.openObject();
        Property propertyMapping;
        for(Map.Entry<String,Property> entry : propertyNameToPropertyMapping.entrySet()) {
            propertyMapping = entry.getValue();
            serializer.field(entry.getKey());
            if(propertyMapping == null) {
                serializer.value(null);
                continue;
            }
            serializer.openObject();
            serializer.fieldValueIfNotNull("name", propertyMapping.getPropertyName());
            serializer.fieldValueIfNotNull("label", propertyMapping.getPropertyLabel());
            serializer.fieldValueIfNotNull("domain", propertyMapping.getPropertyDomain());
            serializer.fieldValueIfNotNull("range", propertyMapping.getPropertyRange());
            serializer.closeObject();
        }
        serializer.closeObject();

        serializer.field("issues");
        if(issues != null) {
            serializer.openList();
            for (String issue : issues) {
                serializer.value(issue);
            }
            serializer.closeList();
        }

        serializer.closeObject();
    }

    protected void addMapping(String propertyName, String property) {
        Property propertyMapping;
        synchronized (ontologyManager) {
            propertyMapping = ontologyManager.getProperty(property);
        }
        if(propertyMapping == null) {
            propertyMapping = new DefaultProperty(property, null, null, null);
        }
        final Property prev = propertyNameToPropertyMapping.put(
                propertyName,
                propertyMapping
        );
        if(prev != null)
            reportIssue (
                String.format(
                    "Property name '%s' already mapped with value %s while adding mapping %s",
                    propertyName, prev, propertyMapping
                )
            );
    }

    public void setOntologyManager(OntologyManager om) {
        this.ontologyManager = om;
    }

    private void reportIssue(String issue) {
        if(issues == null) issues = new ArrayList<>();
        issues.add(issue);
    }

}
