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

import com.machinelinking.util.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * {@link com.machinelinking.storage.elasticsearch.ElasticJSONStorage} test for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticJSONStorageTest extends ElasticJSONStorageTestBase {

    private Logger logger = Logger.getLogger(ElasticJSONStorageTest.class);

    @Test
    public void testLoad() throws IOException {
        final ElasticJSONStorage storage = super.createStorage();

        final Map<String,?> data = JSONUtils.parseJSONAsMap(
                IOUtils.toString(this.getClass().getResourceAsStream("/com/machinelinking/pipeline/Page1.json"))
        );

        long start = 0;
        try (final ElasticJSONStorageConnection connection = storage.openConnection(TEST_COLLECTION)
        ) {
            start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                connection.addDocument(new ElasticDocument(i, i, "doc_" + i, data));
            }
        } finally {
            logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        }
    }

}
