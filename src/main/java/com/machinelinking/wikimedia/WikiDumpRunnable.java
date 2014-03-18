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
