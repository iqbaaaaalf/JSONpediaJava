package com.machinelinking.extractor;


import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.wikimedia.WikimediaUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Defines a <i>Wikipedia reference</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Reference implements Serializable {

    public static final String IMG_PREFIX      = "File";
    public static final String CATEGORY_PREFIX = "Category";

    private URL label;
    private String description;
    private short sectionIndex;

    public static URL labelToURL(URL document, String label) throws MalformedURLException {
        final int prefixIndex = label.indexOf(":");
        final String urlLabel = label.replaceAll(" ", "_");
        if(prefixIndex == -1) {
            return new URL( document.toExternalForm() + urlLabel);
        } else {
            final String prefix = label.substring(0, prefixIndex);
            switch (prefix) {
                case IMG_PREFIX:
                    return new URL(WikimediaUtils.getEntityPath(document.toExternalForm()) + urlLabel);
                case CATEGORY_PREFIX:
                    return new URL(WikimediaUtils.getEntityPath(document.toExternalForm()) + urlLabel);
                default:
                    return new URL(
                            String.format(
                                    "http://%s.wikipedia.org/wiki/%s",
                                    prefix, safeSubstring(urlLabel, prefixIndex + 1)
                            )
                    );
            }
        }
    }

    private static String safeSubstring(String str, int index) {
        if(index < str.length() - 1) {
            return str.substring(index);
        }
        return "";
    }

    public Reference(URL document, String label, String description, short sectionIndex)
    throws MalformedURLException {
        this.label = labelToURL(document, label);
        this.description = description;
        this.sectionIndex = sectionIndex;
    }

    public URL getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public short getSectionIndex() {
        return sectionIndex;
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.openObject();
        serializer.fieldValue("label", label.toExternalForm());
        serializer.fieldValue("description", description);
        serializer.fieldValue("section_idx", sectionIndex);
        serializer.closeObject();
    }

}
