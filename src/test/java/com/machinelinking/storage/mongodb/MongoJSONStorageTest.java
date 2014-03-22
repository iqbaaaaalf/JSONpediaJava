package com.machinelinking.storage.mongodb;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Test case for {@link com.machinelinking.storage.mongodb.MongoJSONStorage}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageTest {

    private Logger logger = Logger.getLogger(MongoJSONStorageTest.class);

    // 10000 items: 46970 ms
    @Ignore
    @Test
    public void testLoad() throws IOException {
        final MongoJSONStorageFactory factory = new MongoJSONStorageFactory();
        final MongoJSONStorage storage = factory.createStorage(
                factory.createConfiguration("localhost:7654:test_load")
        );

        final DBObject dbNode = (DBObject) JSON.parse(
                IOUtils.toString(this.getClass().getResourceAsStream("/com/machinelinking/enricher/Page1.json"))
        );
        long start = 0;
        try (final MongoJSONStorageConnection connection =
                     (MongoJSONStorageConnection) storage.openConnection("test_load_table")
        ) {
            start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                connection.addDocument(new MongoDocument("doc_" + i, i, i, dbNode));
            }
        } finally {
            logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        }
    }

}
