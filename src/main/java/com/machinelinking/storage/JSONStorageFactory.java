package com.machinelinking.storage;

/**
 * Defines the factory for a {@link com.machinelinking.storage.JSONStorageConfiguration}
 * and a {@link com.machinelinking.storage.JSONStorage}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorageFactory<C extends JSONStorageConfiguration, S extends JSONStorage> {

    C createConfiguration(String configURI);

    S createStorage(C config);

}
