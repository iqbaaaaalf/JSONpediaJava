/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

package com.machinelinking.wikimedia;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A <i>Wikipage</i> processing queue implementing {@link WikiPageHandler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class BufferedWikiPageHandler implements WikiPageHandler {

    public static final int MAX_BUFFER_SIZE = 1024 * 1024; // 1MB

    public static final WikiPage EOQ = new WikiPage(0, 0, null, null);

    private final StringBuilder sb = new StringBuilder();
    private final Stack<WikiPage> buffer = new Stack<>();

    private AtomicBoolean closed = new AtomicBoolean(false);

    private int bufferSize = 0;

    private int     id;
    private int     revId;
    private String  title;

    public int size() {
        synchronized (buffer) {
            return buffer.size();
        }
    }

    public int sizeChars() {
        synchronized (buffer) {
            return bufferSize;
        }
    }

    public WikiPage getPage(boolean wait) {
        synchronized (buffer) {
            if (buffer.isEmpty() && closed.get()) {
                return EOQ;
            }

            WikiPage page;
            while (true) {
                page = take();
                if (page == null) {
                    if (wait) {
                        try {
                            buffer.wait();
                        } catch (InterruptedException ie) {
                            throw new RuntimeException("Error while waiting for buffer filling.", ie);
                        }
                    } else {
                        return null;
                    }
                } else {
                    buffer.notifyAll();
                    return page;
                }
            }
        }
    }

    public void reset() {
        sb.delete(0, sb.length());
        synchronized (buffer) {
            buffer.clear();
            bufferSize = 0;
            closed.set(false);
        }
    }

    @Override
    public void startStream() {
        reset();
    }

    @Override
    public void startWikiPage(int id, int revisionId, String title) {
        this.id = id;
        this.revId = revisionId;
        this.title = title;
    }

    @Override
    public void wikiPageContent(char[] buffer, int offset, int len) {
        sb.append(buffer, offset, len);
    }

    @Override
    public void endWikiPage() {
        final String content = sb.toString();
        sb.delete(0, sb.length());
        synchronized (buffer) {
            final WikiPage page = new WikiPage(this.id, this.revId, this.title, content);
            buffer.push(page);
            bufferSize += page.getSize();
            buffer.notifyAll();
            while (bufferSize >= MAX_BUFFER_SIZE) try {
                buffer.notifyAll();
                buffer.wait();
            } catch (InterruptedException ie) {
                throw new RuntimeException("Error while waiting for max size unlock.", ie);
            }
        }
    }

    @Override
    public void endStream() {
        closed.set(true);
        synchronized (buffer) {
            buffer.notifyAll();
        }
    }

    private WikiPage take() {
        if(buffer.isEmpty()) return null;
        final WikiPage page = buffer.pop();
        bufferSize -= page.getSize();
        return page;
    }

}
