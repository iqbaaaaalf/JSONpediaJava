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

import com.machinelinking.wikimedia.WikimediaUtils;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CategoriesKeyValueRender implements KeyValueRender {

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws NodeRenderException {
        final Iterator<JsonNode> categories = value.get("content").getElements();
        try {
            writer.openTag("div");
            writer.heading(1, "Categories");
            final String lang = WikimediaUtils.urlToParts(context.getDocumentContext().getDocumentURL()).lang;
            while (categories.hasNext()) {
                writer.category(lang, categories.next().asText());
            }
            writer.closeTag();
        } catch (IOException ioe) {
            throw new NodeRenderException(ioe);
        }
    }

}
