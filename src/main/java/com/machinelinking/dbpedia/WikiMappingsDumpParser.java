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

package com.machinelinking.dbpedia;

import com.machinelinking.wikimedia.WikiPageHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * A parser for the DBpedia Mappings Wikipage XML API response.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiMappingsDumpParser extends DefaultHandler {

    private static final String PAGE_NODE     = "page";
    private static final String REVISION_NODE = "rev";
    private static final String ALLPAGES_NODE = "allpages";
    private static final String ID_ATTR       = "pageid";
    private static final String TITLE_ATTR    = "title";
    private static final String REV_ATTR      = "revid";
    private static final String GAPFROM_ATTR  = "gapfrom";

    private final SAXParserFactory factory = SAXParserFactory.newInstance();
    private final SAXParser saxParser;

    private WikiPageHandler handler;

    private boolean insidePage = false;
    private boolean insideRevision = false;

    private Integer pageId   = null;
    private Integer pageRev  = null;
    private String pageTitle = null;
    private String gapFrom   = null;

    public WikiMappingsDumpParser() {
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

    public String parse(WikiPageHandler handler, InputStream is) throws IOException, SAXException {
        this.handler = handler;
        insidePage = insideRevision = false;
        gapFrom = null;
        saxParser.parse(is, this);
        return gapFrom;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    throws SAXException {
        if(gapFrom == null && ALLPAGES_NODE.equalsIgnoreCase(qName)) {
            gapFrom = attributes.getValue(GAPFROM_ATTR);
        }
        if(PAGE_NODE.equalsIgnoreCase(qName)) {
            insidePage = true;
            pageId = Integer.parseInt(attributes.getValue(ID_ATTR));
            pageTitle = attributes.getValue(TITLE_ATTR);
        } else if(insidePage && REVISION_NODE.equalsIgnoreCase(qName)) {
            insideRevision = true;
            pageRev = Integer.parseInt(attributes.getValue(REV_ATTR));
            handler.startWikiPage(pageId, pageRev, pageTitle);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (insideRevision) {
            handler.wikiPageContent(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!insideRevision && insidePage && PAGE_NODE.equalsIgnoreCase(qName)) {
            insidePage = false;
            handler.endWikiPage();
        } else if (insideRevision && REVISION_NODE.equalsIgnoreCase(qName)) {
            insideRevision = false;
        }
    }

}
