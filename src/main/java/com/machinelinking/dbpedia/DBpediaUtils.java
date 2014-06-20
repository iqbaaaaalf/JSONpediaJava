package com.machinelinking.dbpedia;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * <i>DBpedia APIs utility methods.</i>
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DBpediaUtils {

    public static final String DBPEDIA_SERVICE = "http://mappings.dbpedia.org";

    private static final String DBPEDIA_GET_MAPPING_CODE_API =
            DBPEDIA_SERVICE + "/api.php?action=query&prop=revisions&rvprop=ids|content&format=xml" +
            "&titles=";

    private static final String DBPEDIA_GET_ALL_MAPPINGS_API =
            DBPEDIA_SERVICE +
                    "/api.php?action=query&generator=allpages&prop=revisions&rvprop=ids|content|timestamp&" +
                    "format=xml&gapnamespace=%d&gaplimit=50%s";


    public static URL templateToWikiMappingAPIURL(String templateName) {
        try {
            return new URL(DBPEDIA_GET_MAPPING_CODE_API + URLEncoder.encode(templateName, "UTF-8"));
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    private DBpediaUtils() {}

    public static URL wikiMappingsAPIURL(int namespace, String gapFrom) {
        try {
            return new URL(
                    String.format(DBPEDIA_GET_ALL_MAPPINGS_API,
                            namespace, gapFrom == null ? "" : "&gapfrom=" + gapFrom.replaceAll(" ", "_")
                    )
            );
        } catch (MalformedURLException murle) {
            throw new IllegalStateException();
        }
    }

}
