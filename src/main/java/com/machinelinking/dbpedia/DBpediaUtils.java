package com.machinelinking.dbpedia;

import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DBpediaUtils {

    private static final String DBPEDIA_GET_MAPPING_WTEXT_CODE_API =
            "http://mappings.dbpedia.org/api.php?action=query&prop=revisions&rvprop=ids|content&format=xml" +
            "&titles=";

    public static URL templateToWikiMappingAPIURL(String templateName) {
        try {
            return new URL(DBPEDIA_GET_MAPPING_WTEXT_CODE_API + URLEncoder.encode(templateName, "UTF-8"));
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    private DBpediaUtils() {}

    public static URL wikiMappingsAPIURL() {
        return null;
    }
}
