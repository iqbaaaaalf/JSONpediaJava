package com.machinelinking.wikimedia;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface PageProcessor {

    void processPage(String pagePrefix, String threadId, WikiPage page);

}
