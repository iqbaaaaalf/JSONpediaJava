package com.machinelinking.wikimedia;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiDumpRunnable implements Runnable {

    private final String threadId;
    private final String pagePrefix;
    private final BufferedWikiPageHandler bufferedHandler;
    private final PageProcessor processor;

    private long processedPages;

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

    public long getProcessedPages() {
        return processedPages;
    }

    @Override
    public void run() {
        WikiPage page;
        while ((page = bufferedHandler.getPage(true)) != BufferedWikiPageHandler.EOQ) {
            processor.processPage(pagePrefix, threadId, page);
            processedPages++;
        }
    }

}
