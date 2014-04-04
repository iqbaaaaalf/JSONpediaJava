package com.machinelinking.storage;

import java.io.Closeable;

/**
 * Defines a <i>JSON</i> storage.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorage<C extends JSONStorageConfiguration, D extends Document> extends Closeable {

    public C getConfiguration();

    DocumentConverter<D> getConverter();

    JSONStorageConnection<D> openConnection(String collection);

    void close();

}
