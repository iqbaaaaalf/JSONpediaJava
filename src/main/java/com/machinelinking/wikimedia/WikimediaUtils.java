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

package com.machinelinking.wikimedia;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Functions to manage <i>Wikimedia</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikimediaUtils {

    private static final String WIKIPEDIA_GET_RESOURCE_WTEXT_CODE_API =
            "/w/api.php?format=xml&action=query&titles=%s&prop=revisions&rvprop=ids|content";

    public static String getEntityName(String entityURL) {
        final int entityNameBegin = entityURL.lastIndexOf('/') + 1;
        if(entityNameBegin == -1) throw new IllegalArgumentException("Cannot detect entity name in URL entityURL.");
        return entityURL.substring(entityNameBegin);
    }

    public static String getEntityTitle(String entityURL) {
        return getEntityName(entityURL).replaceAll("_", " ");
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

    public static URL toTemplateURL(String lang, String label) {
        try {
            return new URL(
                    String.format("http://%s.wikipedia.org/wiki/Template:%s", lang, label.replaceAll(" ", "_"))
            );
        } catch (MalformedURLException murle) {
            throw new IllegalArgumentException("Invalid arguments.", murle);
        }
    }

    public static URL toCategoryURL(String lang, String category) {
        try {
            return new URL(
                    String.format("http://%s.wikipedia.org/wiki/Category:%s", lang, category.replaceAll(" ", "_"))
            );
        } catch (MalformedURLException murle) {
            throw new IllegalArgumentException("Invalid arguments.", murle);
        }
    }

    public static Parts urlToParts(URL url) {
        final String lang = url.getHost().split("\\.")[0];
        final String[] pathParts = url.getPath().split("/");
        final String label = pathParts[pathParts.length - 1];
        return new Parts(lang, label);
    }

    private WikimediaUtils() {}

    public static class Parts {
        public final String lang;
        public final String label;

        Parts(String lang, String label) {
            this.lang = lang;
            this.label = label;
        }
    }

}
