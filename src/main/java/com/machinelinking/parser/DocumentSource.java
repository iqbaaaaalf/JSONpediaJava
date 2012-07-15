package com.machinelinking.parser;

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

    public DocumentSource(URL documentURL) {
        this(documentURL, null);
    }

    public URL getDocumentURL() {
        return documentURL;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

}
