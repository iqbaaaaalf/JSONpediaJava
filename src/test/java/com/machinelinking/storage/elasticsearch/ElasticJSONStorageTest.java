package com.machinelinking.storage.elasticsearch;

import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
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
public class ElasticJSONStorageTest {

    public static String TEST_DB = "test_db";
    public static String TEST_COLLECTION = "test_collection";

    private Logger logger = Logger.getLogger(ElasticJSONStorageTest.class);

    @Test
    public void testLoad() throws IOException {
        final ElasticJSONStorageFactory factory = new ElasticJSONStorageFactory();
        final ElasticJSONStorage storage = factory.createStorage(
                factory.createConfiguration(
                        String.format("localhost:9300:%s:%s", TEST_DB, TEST_COLLECTION)
                )
        );

        final Map<String,?> data = JSONUtils.parseJSONAsMap(
                IOUtils.toString(this.getClass().getResourceAsStream("/com/machinelinking/enricher/Page1.json"))
        );
        removeUnindexableFields(data);

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

    private void removeUnindexableFields(Map<String, ?> data) {
        Assert.assertNotNull(data.remove("wikitext-json"));
        Assert.assertNotNull(data.remove("infobox-splitter"));
        Assert.assertNotNull(data.remove("table-splitter"));
        }

}
