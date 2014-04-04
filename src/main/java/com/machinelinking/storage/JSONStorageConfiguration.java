package com.machinelinking.storage;

/**
 * Defines a <i>JSON</i> storage configuration.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorageConfiguration {

    String getHost();

    int getPort();

    String getDB();

    String getCollection();

}
