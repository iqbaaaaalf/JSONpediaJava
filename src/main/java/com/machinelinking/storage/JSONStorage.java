package com.machinelinking.storage;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorage<D extends Document> {

    JSONStorageConnection<D> openConnection(String table);

    void close();

}
