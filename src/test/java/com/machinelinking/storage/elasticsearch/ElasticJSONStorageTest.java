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
public class ElasticJSONStorageTest extends ElasticJSONStorageTestBase {

    private Logger logger = Logger.getLogger(ElasticJSONStorageTest.class);

    @Test
    public void testLoad() throws IOException {
        final ElasticJSONStorage storage = super.createStorage();

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
