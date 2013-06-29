package com.machinelinking.wikimedia;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * A <i>Wikipage</i> processing queue implementing {@link WikiPageHandler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class BufferedWikiPageHandler implements WikiPageHandler {

    public static final WikiPage EOQ = new WikiPage(0, null, null);

    private final StringBuilder sb = new StringBuilder();
    private final ArrayBlockingQueue<WikiPage> pages = new ArrayBlockingQueue<>(1024);

    private int     id;
    private String  title;
    private boolean eoqAdded = false;

    public int size() {
        return pages.size() - (eoqAdded ? 1 : 0);
    }

    public WikiPage getPage(boolean wait) {
        try {
            if(pages.peek() == EOQ) return EOQ;
            return wait ? pages.take() : pages.poll();
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }

    public void reset() {
        sb.delete(0, sb.length());
        pages.clear();
        eoqAdded = false;
    }

    @Override
    public void startStream() {
        reset();
    }

    @Override
    public void startWikiPage(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public void wikiPageContent(char[] buffer, int offset, int len) {
        sb.append(buffer, offset, len);
    }

    @Override
    public void endWikiPage() {
        try {
            pages.put(new WikiPage(this.id, this.title, sb.toString()));
        } catch (InterruptedException ie) {
            throw new IllegalStateException("Something went wrong.", ie);
        }
        sb.delete(0, sb.length());
    }

    @Override
    public void endStream() {
        pages.add(EOQ);
        eoqAdded = true;
    }

}
