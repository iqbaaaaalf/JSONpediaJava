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
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiAPIParser extends DefaultHandler {

    private static final String PAGE_NODE        = "page";
    private static final String REV_NODE         = "rev";
    private static final String PAGE_ID_ATTR     = "pageid";
    private static final String PAGE_TITLE_ATTR  = "title";

    private final SAXParserFactory factory = SAXParserFactory.newInstance();
    private final SAXParser saxParser;

    private WikiPageHandler handler;

    private boolean insidePage = false;
    private boolean insideRev  = false;

    private int    pageID;
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
            pageID    = Integer.parseInt(attributes.getValue(PAGE_ID_ATTR));
            pageTitle = attributes.getValue(PAGE_TITLE_ATTR);
        } else if(insidePage && REV_NODE.equalsIgnoreCase(qName)) {
            insideRev = true;
            handler.startWikiPage(pageID, pageTitle);
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
