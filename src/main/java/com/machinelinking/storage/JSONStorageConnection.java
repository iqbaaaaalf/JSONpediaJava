package com.machinelinking.storage;

import java.io.Closeable;

/**
 * Defines a <i>JSON</i> storage connection.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorageConnection<D extends Document> extends Closeable {

    void addDocument(D document);

    void removeDocument(String docId);

    D getDocument(String docId);

    long getDocumentsCount();

    void close();

}
