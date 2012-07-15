package com.machinelinking.wikimedia;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiPage {

    private final String id;
    private final String content;

    public WikiPage(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("id: %s\n\n content:%s\n", id, content);
    }
}
