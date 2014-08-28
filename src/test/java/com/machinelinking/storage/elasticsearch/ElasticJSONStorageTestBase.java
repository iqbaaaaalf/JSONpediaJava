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

package com.machinelinking.storage.elasticsearch;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class ElasticJSONStorageTestBase {

    public static int TEST_PORT = 9300;
    public static String TEST_DB = "jsonpedia_test_db";
    public static String TEST_COLLECTION = "test_collection";

    public ElasticJSONStorage createStorage(int port, String db, String collection) {
        final ElasticJSONStorageFactory factory = new ElasticJSONStorageFactory();
        return factory.createStorage(
                factory.createConfiguration(
                        String.format("localhost:%d:%s:%s", port, db, collection)
                )
        );
    }

    public ElasticJSONStorage createStorage() {
        return createStorage(TEST_PORT, TEST_DB, TEST_COLLECTION);
    }

}
