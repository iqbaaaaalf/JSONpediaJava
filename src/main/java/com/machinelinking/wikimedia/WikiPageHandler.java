package com.machinelinking.wikimedia;

/**
 * Defines a <i>Wikipage</i> dump process handler.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface WikiPageHandler {

    void startStream();

    void startWikiPage(int pageId, int revisionId, String title);

    void wikiPageContent(char[] buffer, int offset, int len);

    void endWikiPage();

    void endStream();

}
