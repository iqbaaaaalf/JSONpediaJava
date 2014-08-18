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

import junit.framework.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Test case for {@link com.machinelinking.wikimedia.BufferedWikiPageHandler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class BufferedWikiPageHandlerTest {

    @Test
    public void testWriteOneConsumeMulti() throws InterruptedException {
        for(int i = 0; i < 10; i++) {
            writeOneConsumeMulti();
        }
    }

    private void writeOneConsumeMulti() throws InterruptedException {
        final BufferedWikiPageHandler wikiPageHandler = new BufferedWikiPageHandler();
        final int COUNT = 2000;
        final Thread wThread = new Thread( new PageWriter(COUNT, wikiPageHandler) );
        final PageReader r1 = new PageReader(wikiPageHandler);
        final PageReader r2 = new PageReader(wikiPageHandler);
        final PageReader r3 = new PageReader(wikiPageHandler);
        final PageReader r4 = new PageReader(wikiPageHandler);
        final Thread r1Thread = new Thread(r1);
        final Thread r2Thread = new Thread(r2);
        final Thread r3Thread = new Thread(r3);
        final Thread r4Thread = new Thread(r4);
        r1Thread.start();
        r2Thread.start();
        r3Thread.start();
        r4Thread.start();
        wThread.start();
        wThread.join();
        r1Thread.join();
        r2Thread.join();
        r3Thread.join();
        r4Thread.join();
        Assert.assertEquals(COUNT, r1.getCounter() + r2.getCounter() + r3.getCounter() + r4.getCounter());
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
        private final Random random = new Random();

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
                try {
                    Thread.sleep(0, random.nextInt(2) );
                } catch (InterruptedException ie) {
                    throw new IllegalStateException();
                }
                counter++;
            }
        }
    }

}
