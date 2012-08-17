package com.machinelinking.wikimedia;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface WikiPageHandler {

    void startStream();

    void startWikiPage(int pageId, String title);

    void wikiPageContent(char[] buffer, int offset, int len);

    void endWikiPage();

    void endStream();

}
