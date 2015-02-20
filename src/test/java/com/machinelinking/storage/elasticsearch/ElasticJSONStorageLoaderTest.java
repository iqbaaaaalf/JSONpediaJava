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

import com.machinelinking.storage.AbstractJSONStorageLoaderTest;
import com.machinelinking.storage.JSONStorage;
import org.testng.annotations.Test;

import java.net.UnknownHostException;

/**
 * {@link com.machinelinking.storage.elasticsearch.ElasticJSONStorage} test.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Test
public class ElasticJSONStorageLoaderTest extends AbstractJSONStorageLoaderTest {

    public static final int SERVICE_PORT = 9300;

    @Override
    protected JSONStorage getJSONStorage() throws UnknownHostException {
        return new ElasticJSONStorage(
                new ElasticJSONStorageConfiguration("localhost", SERVICE_PORT, TEST_STORAGE_DB, TEST_STORAGE_COLLECTION),
                new ElasticDocumentConverter()
        );
    }

}
