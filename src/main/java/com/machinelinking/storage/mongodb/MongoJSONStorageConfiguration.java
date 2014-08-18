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

package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.JSONStorageConfiguration;

/**
 * Implementation of {@link com.machinelinking.storage.JSONStorageConfiguration} for <i>MongoDB</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageConfiguration implements JSONStorageConfiguration {

    private final String host;
    private final int    port;
    private final String db;
    private final String collection;

    public MongoJSONStorageConfiguration(String host, int port, String db, String collection) {
        this.host = host;
        this.port = port;
        this.db = db;
        this.collection = collection;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String getDB() {
        return db;
    }

    @Override
    public String getCollection() { return collection; }

}
