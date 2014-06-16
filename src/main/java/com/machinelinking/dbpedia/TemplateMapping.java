package com.machinelinking.dbpedia;

import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.wikimedia.WikiAPIParser;
import com.machinelinking.wikimedia.WikiPage;
import com.machinelinking.wikimedia.WikimediaUtils;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines a mapping between a <i>Wikipedia Template</i> and <i>DBpedia</i> ontology.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateMapping implements Serializable {

    public static final String MAPPING_PREFIX = "Mapping:";

    private final OntologyManager ontologyManager;

    private final String mappingName;

    private final String mappingClass;

    private final Map<String,Property> propertyNameToPropertyMapping;

    private List<String> issues;

    public static TemplateMapping readMappingForTemplate(String mappingName)
    throws IOException, WikiTextParserException, SAXException {
        final URL templateMappingURL = DBpediaUtils.templateToWikiMappingAPIURL(MAPPING_PREFIX + mappingName);
        WikiPage wikiTextMapping;
        try {
            wikiTextMapping = WikiAPIParser.parseAPIResponse(templateMappingURL);
        } catch (Exception e) {
            wikiTextMapping = null;
            e.printStackTrace();
        }

        if (wikiTextMapping != null) {
            final TemplateMapping[] out = new TemplateMapping[1];
            final TemplateMappingHandler handler = new TemplateMappingHandler(mappingName) {
                @Override
                public void handle(TemplateMapping mapping) {
                    out[0] = mapping;
                }
            };
            final WikiTextParser parser = new WikiTextParser(handler);
            parser.parse(templateMappingURL, new ByteArrayInputStream(wikiTextMapping.getContent().getBytes()));
            return out[0];
        } else {
            return null;
        }
    }

    public TemplateMapping(String mappingName, String mappingClass) {
        try {
            this.ontologyManager = OntologyManagerFactory.getInstance().createOntologyManager();
        } catch (OntologyManagerException ome) {
            throw new RuntimeException("Error while initializing ontology manager.", ome);
        }
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
        serializer.fieldValue("__type", "mapping");
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
        Property propertyMapping = ontologyManager.getProperty(property);
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

    private void reportIssue(String issue) {
        if(issues == null) issues = new ArrayList<>();
        issues.add(issue);
    }

}
