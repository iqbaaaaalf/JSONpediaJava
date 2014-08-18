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

import com.machinelinking.storage.AbstractJSONStorageLoaderTest;
import com.machinelinking.storage.JSONStorage;

import java.net.UnknownHostException;

/**
 * {@link com.machinelinking.storage.mongodb.MongoJSONStorage} test.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageLoaderTest extends AbstractJSONStorageLoaderTest {

    @Override
    protected JSONStorage getJSONStorage() throws UnknownHostException {
        return new MongoJSONStorage(
                new MongoJSONStorageConfiguration("127.0.0.1", 7654, "jsonpedia-test", "en"),
                null
        );
    }

}
