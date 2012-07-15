package com.machinelinking.wikimedia;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiDumpParser extends DefaultHandler {

    private static final String PAGE_NODE     = "page";
    private static final String TITLE_NODE    = "title";
    private static final String REDIRECT_NODE = "redirect";
    private static final String TEXT_NODE     = "text";

    private final SAXParserFactory factory = SAXParserFactory.newInstance();
    private final SAXParser saxParser;

    private WikiPageHandler handler;

    private boolean insidePage    = false;
    private boolean insideTitle   = false;
    private boolean insideText    = false;
    private boolean foundRedirect = false;

    private StringBuilder pageTitle = new StringBuilder();

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
        } else if(insidePage && TITLE_NODE.equalsIgnoreCase(qName)) {
            insideTitle = true;
        } else if(insidePage && REDIRECT_NODE.equalsIgnoreCase(qName)) {
            foundRedirect = true;
        } else if(insidePage && !foundRedirect && TEXT_NODE.equalsIgnoreCase(qName)) {
            insideText = true;
            handler.startWikiPage(pageTitle.toString());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (insideTitle) {
            pageTitle.append(ch, start, length);
        } else if(!foundRedirect && insideText) {
            handler.wikiPageContent(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (insidePage && !foundRedirect && TEXT_NODE.equalsIgnoreCase(qName)) {
            insideText = false;
            handler.endWikiPage();
        } else if (insidePage && TITLE_NODE.equalsIgnoreCase(qName)) {
            insideTitle = false;
        } else if (PAGE_NODE.equalsIgnoreCase(qName)) {
            insidePage = false;
            foundRedirect = false;
            pageTitle.delete(0, pageTitle.length());
        }
    }

}
