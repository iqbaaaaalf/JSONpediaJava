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

import com.machinelinking.pagestruct.Ontology;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ListNodeRender implements NodeRender {

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws NodeRenderException {
        final JsonNode content = node.get(Ontology.CONTENT_FIELD);
        if(content == null || content.isNull()) return;
        try {
            writer.openList();
            for (JsonNode item : content) {
                writer.openListItem();
                rootRender.render(context, rootRender, item.get(Ontology.CONTENT_FIELD), writer);
                writer.closeListItem();
            }
            writer.closeList();
        } catch (IOException ioe) {
            throw new NodeRenderException(ioe);
        }
    }
}
