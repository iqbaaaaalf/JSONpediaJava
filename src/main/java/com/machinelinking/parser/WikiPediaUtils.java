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

package com.machinelinking.parser;

/**
 * Set of <i>Wikipedia</i> related utility functions.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiPediaUtils {

    public static final String INFOBOX_TEMPLATE_NAME = "infobox";

    public static String getInfoBoxName(String templateName) {
        final String[] parts = templateName.split("\\s");
        return parts.length >= 2 && INFOBOX_TEMPLATE_NAME.equalsIgnoreCase(parts[0]) ? parts[0] : null;
    }

    private WikiPediaUtils() {}

}
