package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ReferenceNodeRender implements NodeRender {

    public static final String[] IMAGE_EXT = new String[] {"jpg"};

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

    private void writeHTMLURL(String target, String desc, HTMLWriter writer) throws IOException {
        if( isImage(target) ) {
            writer.key(desc);
            writer.image(target, desc);
        } else {
            writer.anchor(target, desc);
        }
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
