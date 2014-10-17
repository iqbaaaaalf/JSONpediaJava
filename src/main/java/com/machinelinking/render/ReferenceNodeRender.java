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

import com.machinelinking.extractor.Reference;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ReferenceNodeRender implements NodeRender {

    private static final String ALT_PREFIX = "alt=";

    private static final Map<String,String> REFERENCE_NODE_ATTR = new HashMap<String,String>(){{
        put("class", "reference");
    }};

    public static void writeReferenceHTML(String lang, String ref, String caption, HTMLWriter writer)
    throws IOException {
         writer.openTag("span", REFERENCE_NODE_ATTR);
        if(Reference.isImage(ref) ) {
            final String[] descSections = caption.split("\\|");
            writer.key(descSections[descSections.length -1]);
            String alt = "";
            for(String descSection : descSections) {
                if(descSection.startsWith(ALT_PREFIX)) {
                    alt = descSection.substring(ALT_PREFIX.length());
                    break;
                }
            }
            writer.image(Reference.imageResourceToURL(ref), alt);
        } else {
            writer.reference(caption, lang, ref, true);
        }
        writer.closeTag();
    }

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws NodeRenderException {
        final String label       = node.get("label").asText();
        final String description = node.get("content").asText().trim();
        try {
            writeReferenceHTML(
                    context.getLang(),
                    label,
                    description.length() == 0 ? label : description,
                    writer
            );
        } catch (IOException ioe) {
            throw new NodeRenderException(ioe);
        }
    }

}
