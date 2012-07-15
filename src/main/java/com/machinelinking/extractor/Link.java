package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;

import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Link implements Serializable {

    private URL link;
    private String description;

    public Link(URL link, String description) {
        this.link = link;
        this.description = description;
    }

    public URL getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.openObject();
        serializer.fieldValue("link", link.toExternalForm());
        serializer.fieldValue("description", description);
        serializer.closeObject();
    }
}
