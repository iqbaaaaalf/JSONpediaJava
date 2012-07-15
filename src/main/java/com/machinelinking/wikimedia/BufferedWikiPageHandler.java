package com.machinelinking.wikimedia;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class BufferedWikiPageHandler implements WikiPageHandler {

    public static final WikiPage EOQ = new WikiPage(null, null);

    private final StringBuilder sb = new StringBuilder();
    private final ArrayBlockingQueue<WikiPage> pages = new ArrayBlockingQueue<>(1024);

    private String documentId;

    public int size() {
        return pages.size() - ( pages.contains(EOQ) ? 1 : 0); //TODO: mmm
    }

    public WikiPage getPage(boolean wait) {
        try {
            final WikiPage out = wait ? pages.take() : pages.poll();
            if(out == EOQ) pages.put(EOQ);
            return out;
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }

    public void reset() {
        sb.delete(0, sb.length());
        pages.clear();
    }

    @Override
    public void startStream() {
        reset();
    }

    @Override
    public void startWikiPage(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public void wikiPageContent(char[] buffer, int offset, int len) {
        sb.append(buffer, offset, len);
    }

    @Override
    public void endWikiPage() {
        try {
            pages.put(new WikiPage(this.documentId, sb.toString()));
        } catch (InterruptedException ie) {
            throw new IllegalStateException("Something went wrong.", ie);
        }
        sb.delete(0, sb.length());
    }

    @Override
    public void endStream() {
        pages.add(EOQ);
    }

}
