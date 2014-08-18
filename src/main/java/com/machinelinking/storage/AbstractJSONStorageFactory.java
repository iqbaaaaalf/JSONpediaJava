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

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class AbstractJSONStorageFactory<C extends JSONStorageConfiguration, S extends JSONStorage, D extends Document> implements JSONStorageFactory<C,S, D> {

    protected Configuration parseConfigurationURI(String configURI) {
        final String[] sections = configURI.split(":");
        if (sections.length != 4)
            throw new IllegalArgumentException("Invalid config URI: must be: <host>:<port>:<db>:<collection>");
        final String host = checkValid(sections[0], "host");
        final int port;
        try {
            port = Integer.parseInt(sections[1]);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid port, must be a number: " + sections[1]);
        }
        final String db = checkValid(sections[2], "db");
        final String collection = checkValid(sections[3], "collection");
        return new Configuration(host, port, db, collection);
    }

    private String checkValid(String in, String desc) {
        if(in.trim().length() == 0) {
            throw new IllegalArgumentException(String.format("Invalid value '%s' for %s", in, desc));
        }
        return in;
    }

    protected class Configuration {
        public final String host;
        public final int port;
        public final String db;
        public final String collection;
        Configuration(String host, int port, String db, String collection) {
            this.host = host;
            this.port = port;
            this.db = db;
            this.collection = collection;
        }
    }

}
