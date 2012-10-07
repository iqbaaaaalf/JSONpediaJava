package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class LinkExtractor extends Extractor {

    private List<Link> links;

    private URL url;
    private StringBuilder linkContent = new StringBuilder();
    private boolean foundParam;

    public LinkExtractor() {
        super("links");
    }

    @Override
    public void beginLink(URL url) {
        this.url = url;
        linkContent.delete(0, linkContent.length());
        foundParam = false;
    }

    @Override
    public void parameter(String param) {
        if(foundParam) linkContent.append("|");
        foundParam = true;
        if(param != null) {
            linkContent.append(param).append("=");
        }
    }

    @Override
    public void text(String content) {
        linkContent.append(content);
    }

    @Override
    public void endLink(URL url) {
        if(this.url == null) throw new IllegalStateException();
        if(links == null) links = new ArrayList<>();
        links.add(new Link(this.url, linkContent.toString()));
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
