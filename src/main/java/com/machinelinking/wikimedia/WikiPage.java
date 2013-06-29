package com.machinelinking.wikimedia;

/**
 * Models a single <i>Wikipage</i>
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiPage {

    private final int    id;
    private final String title;
    private final String content;

    public WikiPage(int id, String title, String content) {
        this.id      = id;
        this.title   = title;
        this.content = content;
    }

    public int getId() {
         return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("id: %d title: %s\n\n content:%s\n", id, title, content);
    }

}
