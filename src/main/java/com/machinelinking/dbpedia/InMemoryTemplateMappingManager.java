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

import com.machinelinking.parser.DocumentSource;
import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.wikimedia.BufferedWikiPageHandler;
import com.machinelinking.wikimedia.WikiPage;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Implementation of {@link com.machinelinking.dbpedia.TemplateMappingManager} keeping data in memory.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InMemoryTemplateMappingManager implements TemplateMappingManager {

    public static final String MAPPING_FILE_PATTERN = "mappings-%s.ser";

    private static final Pattern MAPPING_RE = Pattern.compile("Mapping [a-z]{2,3}:.+");
    private static final Logger logger = Logger.getLogger(InMemoryTemplateMappingManager.class);

    private final Map<String,TemplateMapping> nameToMapping;

    public static File getMappingFile(String lang) {
        return new File(String.format(MAPPING_FILE_PATTERN, lang));
    }

    public static List<TemplateMapping> loadMappingFromAPI(int namespace) throws TemplateMappingManagerException {
        final List<TemplateMapping> result = new ArrayList<>();
        MappingList current = null;
        while(true) {
            current = loadMappingFromAPIPagination(namespace, current == null ? null : current.gapFrom);
            result.addAll(current.mappings);
            if(current.gapFrom == null) break;
        }
        return result;
    }

    private static MappingList loadMappingFromAPIPagination(int namespace, String gapFrom) throws TemplateMappingManagerException {
        try {
            final WikiMappingsDumpParser dumpParser = new WikiMappingsDumpParser();
            final BufferedWikiPageHandler bufferedHandler = new BufferedWikiPageHandler();
            final URL mappingQueryURL = DBpediaUtils.wikiMappingsAPIURL(namespace, gapFrom);
            if(logger.isDebugEnabled())
                logger.debug("Retrieving mappings with query: " + mappingQueryURL);
            final String nextGapFrom = dumpParser.parse(
                    bufferedHandler,
                    mappingQueryURL.openStream()
            );

            final List<TemplateMapping> mappings = new ArrayList<>();
            final TemplateMappingHandler templateMappingHandler = new TemplateMappingHandler(null) {
                @Override
                public void handle(TemplateMapping mapping) {
                    mappings.add(mapping);
                }
            };
            final WikiTextParser wikiTextParser = new WikiTextParser(templateMappingHandler);

            WikiPage page;
            while ((page = bufferedHandler.getPage(false)) != BufferedWikiPageHandler.EOQ) {
                if(! MAPPING_RE.matcher(page.getTitle()).matches()) continue;

                templateMappingHandler.reset(page.getTitle());
                wikiTextParser.reset();
                try {
                    wikiTextParser.parse(
                            new DocumentSource(
                                    new URL(String.format("%s/%s", DBpediaUtils.DBPEDIA_SERVICE, page.getTitle())),
                                    page.getContent()
                            )
                    );
                } catch (IllegalArgumentException iae) { // Conditional mapping failure
                    logger.error("Conditional mapping unsupported.", iae); //TODO: add full support for conditional mappings.
                } catch (WikiTextParserException wtpe) {
                    logger.error("A parse error occurred while reading mapping template", wtpe);
                    if(logger.isDebugEnabled())
                        logger.debug("\nPage source:\n" + page.getContent() + "\n\n");
                }
            }
            return new MappingList(mappings, nextGapFrom);
        } catch (Exception e) {
            throw new TemplateMappingManagerException("Error while parsing Mappings API.", e);
        }
    }

    public static List<TemplateMapping> getMappings(String lang) throws TemplateMappingManagerException {
        final File mappingFile = getMappingFile(lang);
        if(mappingFile.exists()) {
            if(logger.isDebugEnabled())
                logger.debug("Loading mappings from serialization file " + mappingFile);
            return loadMappings(mappingFile);
        } else {
            logger.debug("Loading mappings from DBpedia API");
            try {
                final List<TemplateMapping> mappings = loadMappingFromAPI(Namespaces.getInstance().getNamespace(lang));
                saveMappings(mappings, mappingFile);
                if(logger.isDebugEnabled())
                    logger.debug("Loading mappings from DBpedia API completed. Saved into " + mappingFile);
                return mappings;
            } catch (Exception e) {
                throw new TemplateMappingManagerException("Error while loading mappings.", e);
            }
        }
    }

    private static void saveMappings(List<TemplateMapping> mappings, File file) throws TemplateMappingManagerException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(mappings);
        } catch (IOException ioe) {
            file.delete();
            throw new TemplateMappingManagerException("Error while serializing mappings.", ioe);
        }
    }

    private static List<TemplateMapping> loadMappings(File mappingFile) throws TemplateMappingManagerException {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(mappingFile)))) {
            final List<TemplateMapping> mappings = (List<TemplateMapping>) ois.readObject();
            ois.close();
            return mappings;
        } catch (IOException | ClassNotFoundException ioe) {
            throw new TemplateMappingManagerException("Error while deserializing mappings.", ioe);
        }
    }

    InMemoryTemplateMappingManager(String lang) throws TemplateMappingManagerException {
        final List<TemplateMapping> mappings = getMappings(lang);
        nameToMapping = new HashMap<>();
        for(TemplateMapping mapping : mappings) {
            nameToMapping.put(mapping.getMappingName(), mapping);
        }
    }

    @Override
    public String[] getMappingNames() {
        return nameToMapping.keySet().toArray(new String[0]);
    }

    @Override
    public TemplateMapping getMapping(String templateName) {
        return nameToMapping.get(templateName);
    }

    @Override
    public int getMappingsCount() {
        return nameToMapping.size();
    }

    static class MappingList {
        final List<TemplateMapping> mappings;
        final String gapFrom;
        MappingList(List<TemplateMapping> mappings, String gapFrom) {
            this.mappings = mappings;
            this.gapFrom = gapFrom;
        }
    }

}
