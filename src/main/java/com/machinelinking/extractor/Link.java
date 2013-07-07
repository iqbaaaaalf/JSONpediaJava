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

    private URL link;
    private String description;
    private short sectionIndex;

    public Link(URL link, String description, short sectionIndex) {
        this.link = link;
        this.description = description;
        this.sectionIndex = sectionIndex;
    }

    public URL getLink() {
        return link;
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
        serializer.fieldValue("link", link.toExternalForm());
        serializer.fieldValue("description", description);
        serializer.fieldValue("section_idx", sectionIndex);
        serializer.closeObject();
    }

}
