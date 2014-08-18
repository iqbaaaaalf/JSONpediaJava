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

/**
 * {@link Runnable} implementation for {@link PageProcessor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiDumpRunnable implements Runnable {

    private final String threadId;
    private final String pagePrefix;
    private final BufferedWikiPageHandler bufferedHandler;
    private final PageProcessor processor;

    public WikiDumpRunnable(
            String threadId,
            String pagePrefix,
            BufferedWikiPageHandler bufferedHandler,
            PageProcessor processor
    ) {
        this.threadId = threadId;
        this.pagePrefix = pagePrefix;
        this.bufferedHandler = bufferedHandler;
        this.processor = processor;
    }

    public String getThreadId() {
        return threadId;
    }

    public String getPagePrefix() {
        return pagePrefix;
    }

    @Override
    public void run() {
        WikiPage page;
        while ((page = bufferedHandler.getPage(true)) != BufferedWikiPageHandler.EOQ) {
            processor.processPage(pagePrefix, threadId, page);
        }
    }

}
