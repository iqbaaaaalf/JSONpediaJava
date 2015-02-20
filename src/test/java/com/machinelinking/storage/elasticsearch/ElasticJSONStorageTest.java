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

import com.machinelinking.storage.JSONStorageConnectionException;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * {@link com.machinelinking.storage.elasticsearch.ElasticJSONStorage} test for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticJSONStorageTest extends ElasticJSONStorageTestBase {

    private Logger logger = Logger.getLogger(ElasticJSONStorageTest.class);

    @Test
    public void testAddGetRemove() throws JSONStorageConnectionException {
        final ElasticJSONStorage storage = createStorage();
        final ElasticJSONStorageConnection connection = storage.openConnection(TEST_COLLECTION);

        final int id = Integer.MAX_VALUE;
        final String uuid = UUID.randomUUID().toString();
        final String KEY = "rand_uuid";
        final Map<String,Object> wData = new HashMap<>();
        wData.put(KEY, uuid);

        connection.addDocument(new ElasticDocument(id, 1, "test_rw", wData));
        connection.flush();

        final Map<String,Object> rData = connection.getDocument(id).getContent();
        Assert.assertEquals(uuid, rData.get(KEY).toString());

        connection.removeDocument(id);
        Assert.assertNull(connection.getDocument(id));
    }

    @Test
    public void testLoad() throws IOException, JSONStorageConnectionException {
        final ElasticJSONStorage storage = super.createStorage();

        storage.deleteCollection();

        final Map<String,?> data = JSONUtils.parseJSONAsMap(
                IOUtils.toString(this.getClass().getResourceAsStream("/com/machinelinking/pipeline/Page1.json"))
        );

        long start = 0;
        try (final ElasticJSONStorageConnection connection = storage.openConnection(TEST_COLLECTION)
        ) {
            try {
                connection.getDocumentsCount();
                Assert.fail("Expected exception due connection deletion.");
            } catch (JSONStorageConnectionException e) {
                // OK.
            }

            final int SIZE = 1000;
            start = System.currentTimeMillis();
            for (int i = 0; i < SIZE; i++) {
                connection.addDocument(new ElasticDocument(i, i, "doc_" + i, data));
            }

            for (int i = 0; i < SIZE; i++) {
                Assert.assertEquals("doc_" + i, connection.getDocument(i).getName());
            }
            Assert.assertEquals(SIZE, connection.getDocumentsCount());
        } finally {
            logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        }
    }

}
