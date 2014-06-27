package com.machinelinking.storage;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MultiJSONStorageConfiguration implements JSONStorageConfiguration, Iterable<JSONStorageConfiguration> {

    private final JSONStorageConfiguration[] configurations;

    protected MultiJSONStorageConfiguration(JSONStorageConfiguration[] configurations) {
        if(configurations.length == 0) throw new IllegalArgumentException();
        this.configurations = configurations;
    }

    @Override
    public String getDB() {
        return getSample().getDB();
    }

    @Override
    public String getCollection() {
        return getSample().getCollection();
    }

    @Override
    public Iterator<JSONStorageConfiguration> iterator() {
        return Arrays.asList(configurations).iterator();
    }

    private JSONStorageConfiguration getSample() {
        return configurations[0];
    }
}
