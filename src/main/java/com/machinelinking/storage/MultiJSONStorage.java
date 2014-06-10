package com.machinelinking.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MultiJSONStorage implements JSONStorage<MultiJSONStorageConfiguration, MultiDocument, MultiSelector> {

    private final MultiJSONStorageConfiguration configuration;
    private final DocumentConverter<MultiDocument> converter;

    private final JSONStorage[] internalStorages;

    protected MultiJSONStorage(
            MultiJSONStorageConfiguration configuration,
            DocumentConverter<MultiDocument> converter,
            JSONStorage[] internalStorages
    ) {
        this.configuration = configuration;
        this.converter = converter;
        this.internalStorages = internalStorages;
    }

    @Override
    public MultiJSONStorageConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public DocumentConverter<MultiDocument> getConverter() {
        return converter;
    }

    @Override
    public MultiJSONStorageConnection openConnection(String collection) {
        final List<JSONStorageConnection> connections = new ArrayList<>();
        for(JSONStorage storage : internalStorages) {
            connections.add(storage.openConnection(collection));
        }
        return new MultiJSONStorageConnection(connections.toArray(new JSONStorageConnection[connections.size()]));
    }

    @Override
    public void deleteCollection(String collection) {
        for(JSONStorage storage : internalStorages) {
            storage.deleteCollection(collection);
        }
    }

    @Override
    public void close() {
        for(JSONStorage storage : internalStorages) {
            storage.close();
        }
    }

}
