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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * A parser for the <i>Wikipage XML dump</i> format.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiDumpParser extends DefaultHandler {

    private static final String PAGE_NODE     = "page";
    private static final String REVISION_NODE = "revision";
    private static final String ID_NODE       = "id";
    private static final String TITLE_NODE    = "title";
    private static final String REDIRECT_NODE = "redirect";
    private static final String TEXT_NODE     = "text";

    private final SAXParserFactory factory = SAXParserFactory.newInstance();
    private final SAXParser saxParser;

    private WikiPageHandler handler;

    private boolean insidePage = false;
    private boolean insideRevision = false;
    private boolean insideId = false;
    private boolean insideTitle = false;
    private boolean insideText = false;
    private boolean foundRedirect = false;

    private StringBuilder textBuffer = new StringBuilder();
    private Integer pageId   = null;
    private Integer pageRev  = null;
    private String pageTitle = null;

    public WikiDumpParser() {
        try {
            saxParser = factory.newSAXParser();
        } catch (Exception e) {
            throw new IllegalStateException("Error while initializing Dump parser.", e);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        handler.startStream();
    }

    @Override
    public void endDocument() throws SAXException {
        handler.endStream();
    }

    public void parse(WikiPageHandler handler, InputStream is) throws IOException, SAXException {
        this.handler = handler;
        insidePage = insideTitle = insideText = foundRedirect = false;
        saxParser.parse(is, this);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    throws SAXException {
        if(PAGE_NODE.equalsIgnoreCase(qName)) {
            insidePage = true;
        } else if(insidePage && REVISION_NODE.equalsIgnoreCase(qName)) {
            insideRevision = true;
        } else if(insidePage && ID_NODE.equalsIgnoreCase(qName)) {
            insideId = true;
            textBuffer.delete(0, textBuffer.length());
        } else if(insidePage && TITLE_NODE.equalsIgnoreCase(qName)) {
            insideTitle = true;
        } else if(insidePage && REDIRECT_NODE.equalsIgnoreCase(qName)) {
            foundRedirect = true;
        } else if(insidePage && !foundRedirect && TEXT_NODE.equalsIgnoreCase(qName)) {
            insideText = true;
            handler.startWikiPage(pageId, pageRev, pageTitle);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!foundRedirect && (insideId || insideTitle)) {
            textBuffer.append(ch, start, length);
        } else if(foundRedirect) {
            textBuffer.append(ch, start, length);
        } else if(insideText) {
            handler.wikiPageContent(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (insidePage && !foundRedirect && TEXT_NODE.equalsIgnoreCase(qName)) {
            insideText = false;
            handler.endWikiPage();
        } else if (insidePage && !insideRevision && ID_NODE.equalsIgnoreCase(qName)) {
            pageId = Integer.parseInt(textBuffer.toString());
            insideId = false;
        } else if (insideRevision && ID_NODE.equalsIgnoreCase(qName)) {
            pageRev = Integer.parseInt(textBuffer.toString());
            insideId = false;
        } else if (insidePage && TITLE_NODE.equalsIgnoreCase(qName)) {
            pageTitle = textBuffer.toString();
            insideTitle = false;
        } else if (REVISION_NODE.equalsIgnoreCase(qName)) {
            insideRevision = false;
        } else if (PAGE_NODE.equalsIgnoreCase(qName)) {
            insidePage = false;
            foundRedirect = false;
            textBuffer.delete(0, textBuffer.length());
        }
    }

}
