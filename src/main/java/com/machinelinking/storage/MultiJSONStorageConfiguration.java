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
