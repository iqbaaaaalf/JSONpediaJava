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

package com.machinelinking.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Defines the source for a <i>Wikitext</i> page.
 *
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
