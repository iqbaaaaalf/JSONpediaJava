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

import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.wikimedia.WikiAPIParser;
import com.machinelinking.wikimedia.WikiPage;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Factory for {@link com.machinelinking.dbpedia.TemplateMappingManager}s
 * and {@link com.machinelinking.dbpedia.TemplateMapping}s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
//TODO: factory is not using template cache currently. Need to be refactored.
public class TemplateMappingFactory {

    private static final Logger logger = Logger.getLogger(TemplateMappingFactory.class);

    private static final String MAPPING_PREFIX = "Mapping:";
    private static final OntologyManager ontologyManager;
    private static TemplateMappingFactory instance;

    static {
        try {
            ontologyManager = OntologyManagerFactory.getInstance().createOntologyManager();
        } catch (OntologyManagerException ome) {
            throw new RuntimeException("Error while initializing ontology manager.", ome);
        }
    }

    public static TemplateMappingFactory getInstance() {
        if(instance == null) {
            instance = new TemplateMappingFactory();
        }
        return instance;
    }

    private TemplateMappingFactory() {}

    public TemplateMappingManager getTemplateMappingManager(String lang) throws TemplateMappingManagerException {
        return new InMemoryTemplateMappingManager(lang);
    }

    public TemplateMapping readMappingForTemplate(String mappingName)
    throws IOException, WikiTextParserException, SAXException {
        final URL templateMappingURL = DBpediaUtils.templateToWikiMappingAPIURL(MAPPING_PREFIX + mappingName);
        WikiPage wikiTextMapping;
        try {
            wikiTextMapping = WikiAPIParser.parseAPIResponse(templateMappingURL);
        } catch (Exception e) {
            wikiTextMapping = null;
            logger.warn(String.format("An error occurred while retrieving mapping [%s]", mappingName), e);
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

    public TemplateMapping createMapping(String mappingName, String mappingClass) {
        final TemplateMapping tm = new TemplateMapping(mappingName, mappingClass);
        tm.setOntologyManager(ontologyManager);
        return tm;
    }

}
