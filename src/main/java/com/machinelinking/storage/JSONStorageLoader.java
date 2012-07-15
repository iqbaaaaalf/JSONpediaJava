package com.machinelinking.storage;

import com.machinelinking.WikiEnricherFactory;
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

    StorageLoaderReport process(URL pagePrefix, InputStream is) throws IOException, SAXException;

}
