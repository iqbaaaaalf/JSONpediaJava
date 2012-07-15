package com.machinelinking.wikimedia;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface WikiPageHandler {

    void startStream();

    void startWikiPage(String documentId);

    void wikiPageContent(char[] buffer, int offset, int len);

    void endWikiPage();

    void endStream();

}
