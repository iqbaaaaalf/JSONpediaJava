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
import java.net.URL;

/**
 * A parser for the <i>Wikipage XML API</i> format.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiAPIParser extends DefaultHandler {

    private static final String PAGE_NODE        = "page";
    private static final String REV_NODE         = "rev";
    private static final String PAGE_ID_ATTR     = "pageid";
    private static final String PAGE_REV_ATTR    = "revid";
    private static final String PAGE_TITLE_ATTR  = "title";

    private final SAXParserFactory factory = SAXParserFactory.newInstance();
    private final SAXParser saxParser;

    private WikiPageHandler handler;

    private boolean insidePage = false;
    private boolean insideRev  = false;

    private int    pageID;
    private int revId;
    private String pageTitle;

    public static WikiPage parseAPIResponse(URL wikiAPIURL) throws IOException, SAXException {
        final WikiAPIParser parser = new WikiAPIParser();
        final BufferedWikiPageHandler bufferedHandler = new BufferedWikiPageHandler();
        parser.parse(bufferedHandler, wikiAPIURL.openStream() );
        if(bufferedHandler.size() != 1)
            throw new IllegalStateException(
                    String.format(
                            "Expected 1 result, found %d while performing call [%s]",
                            bufferedHandler.size(),
                            wikiAPIURL
                    )
            );
        return bufferedHandler.getPage(true);
    }

    public WikiAPIParser() {
        try {
            saxParser = factory.newSAXParser();
        } catch (Exception e) {
            throw new IllegalStateException("Error while initializing API parser.", e);
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
        if(handler == null) throw new NullPointerException();
        if(is == null)      throw new NullPointerException();

        this.handler = handler;
        insidePage = insideRev = false;
        saxParser.parse(is, this);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(PAGE_NODE.equalsIgnoreCase(qName)) {
            insidePage = true;
            try {
                pageID = Integer.parseInt(attributes.getValue(PAGE_ID_ATTR));
                pageTitle = attributes.getValue(PAGE_TITLE_ATTR);
            } catch (Exception e) {
                throw new WikiAPIParserException("Invalid page metadata.");
            }
        } else if(insidePage && REV_NODE.equalsIgnoreCase(qName)) {
            insideRev = true;
            revId = Integer.parseInt(attributes.getValue(PAGE_REV_ATTR));
            handler.startWikiPage(pageID, revId, pageTitle);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (REV_NODE.equalsIgnoreCase(qName)) {
            insideRev = false;
            handler.endWikiPage();
        } else if(PAGE_NODE.equalsIgnoreCase(qName)) {
            insidePage = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (insideRev) {
            handler.wikiPageContent(ch, start, length);
        }
    }

}
