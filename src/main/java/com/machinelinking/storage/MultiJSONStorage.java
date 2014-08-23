/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

package com.machinelinking.storage;

import java.util.ArrayList;
import java.util.Arrays;
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

    public List<JSONStorage> getInternalStorages() {
        return new ArrayList<>(Arrays.asList(internalStorages));
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
    public boolean exists() {
        return exists(null);
    }

    @Override
    public boolean exists(String collection) {
        final String targetConnection = collection == null ? configuration.getCollection() : collection;
        boolean exist, notexist; exist = notexist = false;
        for (JSONStorage storage : internalStorages) {
            if(storage.exists(targetConnection))
                exist = true;
            else
                notexist = true;
        }
        if(exist && notexist)throw new IllegalStateException();
        return exist;
    }

    @Override
    public JSONStorageConnection<MultiDocument, MultiSelector> openConnection() {
        return openConnection(null);
    }

    @Override
    public MultiJSONStorageConnection openConnection(String collection) {
        final String targetCollection = collection == null ? configuration.getCollection() : collection;
        final List<JSONStorageConnection> connections = new ArrayList<>();
        for(JSONStorage storage : internalStorages) {
            connections.add(storage.openConnection(targetCollection));
        }
        return new MultiJSONStorageConnection(connections.toArray(new JSONStorageConnection[connections.size()]));
    }

    @Override
    public void deleteCollection() {
        deleteCollection(null);
    }

    @Override
    public void deleteCollection(String collection) {
        final String targetConnection = collection == null ? configuration.getCollection() : collection;
        for(JSONStorage storage : internalStorages) {
            storage.deleteCollection(targetConnection);
        }
    }

    @Override
    public void close() {
        for(JSONStorage storage : internalStorages) {
            storage.close();
        }
    }

}
