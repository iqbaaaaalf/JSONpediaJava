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

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ReferenceNodeRender implements NodeRender {

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws NodeRenderException {
        final String label       = node.get("label").asText();
        final String description = node.get("content").asText().trim();
        final String[] labelSections = label.split(".");
        try {
            if (labelSections.length == 2) {
                Reference.writeReferenceHTML(
                        String.format("http://%s.wikipedia.org/wiki/%s", labelSections[0], labelSections[1]),
                        description,
                        writer
                );
            } else if (labelSections.length == 0) {
                Reference.writeReferenceHTML(
                        String.format("http://en.wikipedia.org/wiki/%s", label),
                        description.length() == 0 ? label : description,
                        writer
                );
            } else {
                throw new IllegalArgumentException("Invalid label: " + label);
            }
        } catch (IOException ioe) {
            throw new NodeRenderException(ioe);
        }
    }

}
