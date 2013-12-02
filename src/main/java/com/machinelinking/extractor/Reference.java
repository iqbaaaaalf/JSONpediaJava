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

    private URL url;
    private String description;
    private short sectionIndex;

    public static URL labelToURL(URL document, String url) throws MalformedURLException {
        final int prefixIndex = url.indexOf(":");
        final String urlLabel = url.replaceAll(" ", "_");
        if(prefixIndex == -1) {
            final String documentString = document.toExternalForm();
            final String documentPrefix = documentString.substring(0, documentString.lastIndexOf('/') + 1);
            return new URL(documentPrefix + urlLabel);
        } else {
            final String prefix = url.substring(0, prefixIndex);
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

    // TODO: missing IMAGE and CATEGORY management.
    public static String[] urlToLabel(String in) {
        final String HTTP_PREFIX = "http://";
        if(in.startsWith(HTTP_PREFIX)) {
            return new String[] {
                    in.substring(HTTP_PREFIX.length(), in.indexOf('.')),
                    in.substring(in.lastIndexOf('/') + 1)
            };
        } else {
            return in.split(":");
        }
    }

    public static String toURLString(String lang, String label) {
        return String.format("http://%s.wikipedia.org/wiki/%s", lang, label);
    }

    private static String safeSubstring(String str, int index) {
        if(index < str.length() - 1) {
            return str.substring(index);
        }
        return "";
    }

    public Reference(URL document, String label, String description, short sectionIndex)
    throws MalformedURLException {
        this.url = labelToURL(document, label);
        this.description = description;
        this.sectionIndex = sectionIndex;
    }

    public URL getUrl() {
        return url;
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
        serializer.fieldValue("url", url.toExternalForm());
        serializer.fieldValue("description", description);
        serializer.fieldValue("section_idx", sectionIndex);
        serializer.closeObject();
    }

}
