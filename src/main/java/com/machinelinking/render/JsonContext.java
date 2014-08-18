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

package com.machinelinking.render;

import com.machinelinking.util.JsonPathBuilder;
import org.codehaus.jackson.JsonNode;

/**
 * Maintains the current context of a {@link org.codehaus.jackson.JsonNode} visit.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JsonContext {

    DocumentContext getDocumentContext();

    String getDocumentTitle();

    String getLang();

    String getDomain();

    String getJSONPath();

    JsonNode getRoot();

    boolean subPathOf(JsonPathBuilder builder, boolean strict);

}
