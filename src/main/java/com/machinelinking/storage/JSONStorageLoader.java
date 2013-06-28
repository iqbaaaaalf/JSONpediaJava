package com.machinelinking.storage;

import com.machinelinking.enricher.WikiEnricherFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Defines a processor to massive load a <i>Wikitext dump</i> stream into a {@link JSONStorage}
 * the {@link com.machinelinking.enricher.WikiEnricher} built by the specified {@link WikiEnricherFactory}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorageLoader {

    WikiEnricherFactory getEnricherFactory();

    JSONStorage getStorage();

    StorageLoaderReport load(URL pagePrefix, InputStream is) throws IOException, SAXException;

}
