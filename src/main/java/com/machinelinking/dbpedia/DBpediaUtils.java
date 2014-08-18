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
