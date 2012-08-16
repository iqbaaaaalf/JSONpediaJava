package com.machinelinking.storage;

import com.machinelinking.enricher.WikiEnricherFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorageLoader {

    WikiEnricherFactory getEnricherFactory();

    JSONStorage getStorage();

    StorageLoaderReport load(URL pagePrefix, InputStream is) throws IOException, SAXException;

}
