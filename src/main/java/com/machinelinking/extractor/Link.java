package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;

import java.net.URL;

/**
 * Defines a <i>Wikipedia link</i>s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Link implements Serializable {

    private URL url;
    private String description;
    private short sectionIndex;

    public Link(URL url, String description, short sectionIndex) {
        this.url = url;
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
