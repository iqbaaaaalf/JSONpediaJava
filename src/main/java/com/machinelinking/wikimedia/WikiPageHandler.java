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
