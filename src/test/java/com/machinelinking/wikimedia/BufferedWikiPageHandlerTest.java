package com.machinelinking.wikimedia;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Test case for {@link com.machinelinking.wikimedia.BufferedWikiPageHandler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class BufferedWikiPageHandlerTest {

    @Test
    public void testWriteOneConsumeMulti() throws InterruptedException {
        for(int i = 0; i < 20; i++) {
            writeOneConsumeMulti();
        }
    }

    private void writeOneConsumeMulti() throws InterruptedException {
        final BufferedWikiPageHandler wikiPageHandler = new BufferedWikiPageHandler();
        final int COUNT = 20000;
        final Thread wThread = new Thread( new PageWriter(COUNT, wikiPageHandler) );
        final PageReader r1 = new PageReader(wikiPageHandler);
        final PageReader r2 = new PageReader(wikiPageHandler);
        final Thread r1Thread = new Thread(r1);
        final Thread r2Thread = new Thread(r2);
        r1Thread.start();
        r2Thread.start();
        wThread.start();
        wThread.join();
        r1Thread.join();
        r2Thread.join();
        Assert.assertEquals(COUNT, r1.getCounter() + r2.getCounter());
    }

    class PageWriter implements Runnable {

        private final int total;
        private final BufferedWikiPageHandler handler;

        PageWriter(int count, BufferedWikiPageHandler handler) {
            this.total = count;
            this.handler = handler;
        }

        @Override
        public void run() {
            this.handler.startStream();
            int pageId = 0;
            for(int i = 0; i < total; i++) {
                if(Thread.interrupted()) break;
                this.handler.startWikiPage(pageId, 1, "page " + pageId);
                this.handler.wikiPageContent(new char[0], 0, 0);
                this.handler.endWikiPage();
                pageId++;
            }
            this.handler.endStream();
        }

    }

    class PageReader implements Runnable {

        private final BufferedWikiPageHandler handler;
        private int counter;

        PageReader(BufferedWikiPageHandler handler) {
            this.handler = handler;
        }

        public int getCounter() {
            return counter;
        }

        @Override
        public void run() {
            counter = 0;
            while (handler.getPage(true) != BufferedWikiPageHandler.EOQ) {
                counter++;
            }
        }
    }

}
