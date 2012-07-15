package com.machinelinking.wikimedia;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikimediaUtils {

    private static final String WIKIPEDIA_GET_RESOURCE_WTEXT_CODE_API =
            "/w/api.php?format=xml&action=query&titles=%s&prop=revisions&rvprop=content";

    private static final String DBPEDIA_GET_MAPPING_WTEXT_CODE_API =
            "http://mappings.dbpedia.org/api.php?action=query&prop=revisions&rvprop=content&format=xml" +
            "&gapnamespace=204&gaplimit=10&titles=";

    public static String getEntityName(String entityURL) {
        final int entityNameBegin = entityURL.lastIndexOf('/') + 1;
        if(entityNameBegin == -1) throw new IllegalArgumentException("Cannot detect entity name in URL entityURL.");
        return entityURL.substring(entityNameBegin);
    }

    public static String getEntityPath(String entityURL) {
        int lastSlash = entityURL.lastIndexOf("/");
        if(lastSlash == -1) throw new IllegalArgumentException("Cannot extract entity path from URL: " + entityURL);
        return entityURL.substring(0, lastSlash + 1);
    }

    public static URL entityToWikiTextURLAPI(URL entity) {
        try {
            return new URL(
                    "http",
                    entity.getHost(),
                    entity.getPort(),
                    String.format(WIKIPEDIA_GET_RESOURCE_WTEXT_CODE_API, getEntityName(entity.getPath()))
            );
        } catch (MalformedURLException murle) {
            throw new IllegalStateException();
        }
    }

    public static URL templateToWikiMappingURLAPI(String templateName) {
        try {
            return new URL(DBPEDIA_GET_MAPPING_WTEXT_CODE_API + URLEncoder.encode(templateName, "UTF-8"));
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    private WikimediaUtils() {}



}
