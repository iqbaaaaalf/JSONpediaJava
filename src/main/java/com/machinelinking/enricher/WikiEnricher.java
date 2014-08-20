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

package com.machinelinking.enricher;

import com.machinelinking.extractor.Extractor;
import com.machinelinking.pagestruct.PageStructConsts;
import com.machinelinking.pagestruct.WikiTextSerializerHandler;
import com.machinelinking.pagestruct.WikiTextSerializerHandlerFactory;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.parser.MultiWikiTextParserHandler;
import com.machinelinking.parser.ValidatingWikiTextParserHandler;
import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.parser.WikiTextParserHandler;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.splitter.Splitter;
import com.machinelinking.splitter.WikiTextParserHandlerSplitter;
import com.machinelinking.wikimedia.BufferedWikiPageHandler;
import com.machinelinking.wikimedia.WikiAPIParser;
import com.machinelinking.wikimedia.WikimediaUtils;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * The <i>Wiki</i> processor facade.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
// TODO: rename in Pipeline
public class WikiEnricher {

    private final WikiAPIParser apiParser = new WikiAPIParser();

    private final List<Extractor> extractors = new ArrayList<Extractor>();

    private final List<Splitter> splitters   = new ArrayList<Splitter>();

    private final BufferedWikiPageHandler bufferedAPIHandler = new BufferedWikiPageHandler();

    private boolean validate         = false;
    private boolean produceStructure = true;

    public WikiEnricher() {}

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public boolean isProduceStructure() {
        return produceStructure;
    }

    public void setProduceStructure(boolean produceStructure) {
        this.produceStructure = produceStructure;
    }

    public boolean addExtractor(Extractor e) {
        return extractors.add(e);
    }

    public boolean removeExtractor(Extractor e) {
        return extractors.remove(e);
    }

    public List<Extractor> getExtractors() {
        return Collections.unmodifiableList(extractors);
    }

    public boolean addSplitter(Splitter s) {
        return splitters.add(s);
    }

    public boolean removeSplitter(Splitter s) {
        return splitters.remove(s);
    }

    public List<Splitter> getSplitters() {
        return Collections.unmodifiableList(splitters);
    }

    public void enrichEntity(DocumentSource source, Serializer serializer)
    throws SAXException, IOException, WikiTextParserException, InterruptedException, ExecutionException {
        try {
            serializer.openObject();

            serializer.fieldValue(PageStructConsts.TYPE_FIELD, PageStructConsts.TYPE_ENRICHED_ENTITY);

            // Write Document Serialization.
            writeDocumentSerialization(source, serializer);

            // Write extractors serialization.
            for (Extractor extractor : extractors) {
                serializer.field(extractor.getName());
                extractor.flushContent(serializer);
            }

            // Write splitters serialization.
            for(Splitter splitter : splitters) {
                splitter.serialize(serializer);
            }

            serializer.closeObject();
        } finally {
            serializer.close();
        }
    }

    private void writeDocumentSerialization(DocumentSource source, Serializer serializer)
    throws IOException, SAXException, WikiTextParserException {
        final InputStream wikiTextInputStream;
        if(source.getInputStream() != null) {
            wikiTextInputStream = source.getInputStream();
        } else {
            final URL wikiAPIRequest = WikimediaUtils.entityToWikiTextURLAPI(source.getDocumentURL());
            final InputStream wikiAPIInputStream = wikiAPIRequest.openStream();
            bufferedAPIHandler.reset();
            apiParser.parse(bufferedAPIHandler, wikiAPIInputStream);
            wikiTextInputStream = new ByteArrayInputStream(bufferedAPIHandler.getPage(true).getContent().getBytes());
        }

        final WikiTextSerializerHandler serializerHandler =
                WikiTextSerializerHandlerFactory.getInstance().createSerializerHandler(serializer);
        final MultiWikiTextParserHandler multiHandler = new MultiWikiTextParserHandler();
        if(produceStructure) {
            multiHandler.add(serializerHandler);
        }
        for(Extractor extractor : extractors) { // Adding specific extractors.
            extractor.reset();
            multiHandler.add( wrapWithValidator("validator-" + extractor.getName(), extractor) );
        }

        final WikiTextParserHandlerSplitter handlerSplitter = new WikiTextParserHandlerSplitter();
        // NOTE: the handlerSplitter must be notified before the Splitters.
        multiHandler.add( handlerSplitter.getProxy() );
        for(Splitter splitter : splitters) {
            splitter.reset();
            splitter.initHandlerSplitter(handlerSplitter);
            multiHandler.add( wrapWithValidator("splitter-" + splitter.getName(), splitter) );
        }

        final WikiTextParser wikiTextParser = new WikiTextParser( wrapWithValidator("parser", multiHandler) );
        if(produceStructure) {
            //TODO: add page id and other metadata.
             serializer.field(PageStructConsts.PAGE_STRUCT_FIELD);
             serializer.openList();
         }
        wikiTextParser.parse(
                source.getDocumentURL(),
                wikiTextInputStream
        );
        if(produceStructure) {
            serializer.closeList();
        }
    }

    private WikiTextParserHandler wrapWithValidator(String name, WikiTextParserHandler h) {
        if( isValidate() ) {
            return new ValidatingWikiTextParserHandler(name, h).getProxy();
        } else {
            return h;
        }
    }

}
