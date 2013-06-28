package com.machinelinking.storage;

/**
 * Defines a <i>JSON</i> storage.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorage<C extends JSONStorageConfiguration, D extends Document> {

    public C getConfiguration();

    JSONStorageConnection<D> openConnection(String table);

    void close();

}
