package com.machinelinking.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DocumentSource {

    private URL documentURL;
    private InputStream inputStream;

    public DocumentSource(URL documentURL, InputStream inputStream) {
        if(documentURL == null) throw new NullPointerException("documentURL cannot be null.");
        this.documentURL = documentURL;
        this.inputStream = inputStream;
    }

    public DocumentSource(URL documentURL, String wikitext) {
        if(documentURL == null) throw new NullPointerException("documentURL cannot be null.");
        if(wikitext == null) throw new NullPointerException("WikiText cannot be null");
        this.documentURL = documentURL;
        if(wikitext == null) {
            this.inputStream = null;
        } else {
            this.inputStream = new ByteArrayInputStream( wikitext.getBytes() );
        }
    }

    public DocumentSource(URL documentURL) {
        this(documentURL, (InputStream) null);
    }

    public URL getDocumentURL() {
        return documentURL;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

}
