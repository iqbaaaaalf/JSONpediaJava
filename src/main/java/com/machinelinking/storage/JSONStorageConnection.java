package com.machinelinking.storage;

import com.machinelinking.wikimedia.WikiPage;

import java.io.Closeable;

/**
 * Defines a <i>JSON</i> storage connection.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorageConnection<D extends Document, S extends Selector> extends Closeable {

    D createDocument(WikiPage page, String json) throws JSONStorageConnectionException;

    void addDocument(D document) throws JSONStorageConnectionException;

    void removeDocument(int id) throws JSONStorageConnectionException;

    D getDocument(int id) throws JSONStorageConnectionException;

    long getDocumentsCount() throws JSONStorageConnectionException;

    ResultSet<D> query(S selector, int limit) throws JSONStorageConnectionException;

    void flush();

    void close();

}
