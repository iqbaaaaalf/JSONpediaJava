package com.machinelinking.storage;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorageConnection<D extends Document> {

    void addDocument(D document);

    void removeDocument(String docId);

    D getDocument(String docId);

    long getDocumentsCount();

    void addDocument(DBObjectDocument document);
}
