package com.machinelinking.wikimedia;

/**
 * Defines a processor for a <i>Wikipage</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface PageProcessor {

    void processPage(String pagePrefix, String threadId, WikiPage page);

    long getProcessedPages();

    long getErrorPages();

}
