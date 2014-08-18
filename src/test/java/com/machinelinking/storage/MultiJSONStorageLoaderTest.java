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

import java.net.UnknownHostException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MultiJSONStorageLoaderTest extends AbstractJSONStorageLoaderTest {

    public static final String CONFIG_URI =
            "com.machinelinking.storage.mongodb.MongoJSONStorageFactory|localhost:7654:jsonpedia:en;" +
            "com.machinelinking.storage.elasticsearch.ElasticJSONStorageFactory|localhost:9300:jsonpedia:en";

    @Override
    protected JSONStorage getJSONStorage() throws UnknownHostException {
        final MultiJSONStorageFactory factory = new MultiJSONStorageFactory();
        final MultiJSONStorageConfiguration config = factory.createConfiguration(CONFIG_URI);
        return factory.createStorage(config);
    }
}
