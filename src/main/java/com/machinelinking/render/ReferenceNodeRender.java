package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ReferenceNodeRender implements NodeRender {

    public static final String[] IMAGE_EXT = new String[] {"jpg"};

    private static final String ALT_PREFIX = "alt=";

    private static final Map<String,String> REFERENCE_DIV_ATTR = new HashMap<String,String>(){{
        put("style", "background-color: #C971AE");
    }};

    @Override
    public boolean acceptNode(JsonNode node) {
        return true;
    }

    @Override
    public void render(RootRender rootRender, JsonNode node, HTMLWriter writer) throws IOException {
        final String label       = node.get("label").asText();
        final String description = node.get("description").asText().trim();
        final String[] labelSections = label.split(".");
        if(labelSections.length == 2) {
            writeHTMLURL(
                    String.format("http://%s.wikipedia.org/wiki/%s", labelSections[0], labelSections[1]),
                    description,
                    writer
            );
        } else if(labelSections.length == 0) {
            writeHTMLURL(
                    String.format("http://en.wikipedia.org/wiki/%s", label),
                    description.length() == 0 ? label : description,
                    writer
            );
        } else {
            throw new IllegalArgumentException("Invalid label: " + label);
        }
    }

    private void writeHTMLURL(String target, String label, HTMLWriter writer) throws IOException {
         writer.openTag("div", REFERENCE_DIV_ATTR);
        if( isImage(target) ) {
            final String[] descSections = label.split("\\|");
            writer.key(descSections[descSections.length -1]);
            String alt = "";
            for(String descSection : descSections) {
                if(descSection.startsWith(ALT_PREFIX)) {
                    alt = descSection.substring(ALT_PREFIX.length());
                    break;
                }
            }
            writer.image(target, alt);
        } else {
            writer.anchor(target, label, true);
        }
        writer.closeTag();
    }

    private boolean isImage(String url) {
        final String urlLower = url.toLowerCase();
        for(String ext : IMAGE_EXT) {
            if(urlLower.endsWith("." + ext)) {
                return true;
            }
        }
        return false;
    }

}
