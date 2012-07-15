package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class LinkExtractor extends Extractor {

    private List<Link> links;

    public LinkExtractor() {
        super("links");
    }

    @Override
    public void link(String url, String description) {
        if(links == null) links = new ArrayList<Link>();
        try {
            links.add( new Link(new URL(url), description) );
        } catch (MalformedURLException murle) {
            throw new RuntimeException("Error while validating link '" + url + "'", murle);
        }
    }

    @Override
    public void flushContent(Serializer serializer) {
        if(links == null) {
            serializer.value(null);
            return;
        }
        serializer.openList();
        for(Link link : links) {
            link.serialize(serializer);
        }
        serializer.closeList();
        links.clear();
    }

    @Override
    public void reset() {
        if(links != null)links.clear();
    }
}
