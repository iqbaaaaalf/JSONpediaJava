package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.Criteria;
import com.machinelinking.storage.JSONStorageConnection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;

/**
 * Test case for {@link com.machinelinking.storage.mongodb.MongoJSONStorage}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageTest {

    private String TEST_COLLECTION = "test_load_table";

    private Logger logger = Logger.getLogger(MongoJSONStorageTest.class);

    // 10000 items: 46970 ms
    @Test
    public void testLoad() throws IOException {
        loadAndCheck(100);
    }

    @Test
    public void testQuery() throws IOException {
        loadAndCheck(10);

        final MongoJSONStorage storage = getStorage();
        final JSONStorageConnection connection = storage.openConnection(TEST_COLLECTION);
        final MongoSelector selector = new MongoSelector();
        selector.addCriteria( new Criteria("version", Criteria.Operator.lte, 0));
        selector.addCriteria( new Criteria("content.categories.content", Criteria.Operator.eq, "Cosmologists"));
        selector.addCriteria( new Criteria("content.sections.title", Criteria.Operator.eq, "Biography"));
        selector.addProjection("_id");
        selector.addProjection("name");
        selector.addProjection("content");
        final MongoResultSet rs = (MongoResultSet) connection.query(selector, 1000);
        int found = 0;
        while((rs.next()) != null) {
            found++;
        }
        Assert.assertEquals(1, found);
    }

    private void loadAndCheck(final int count) throws IOException {
        final MongoJSONStorage storage = getStorage();
        storage.deleteCollection(TEST_COLLECTION);
        final DBObject dbNode = (DBObject) JSON.parse(
                IOUtils.toString(this.getClass().getResourceAsStream("/com/machinelinking/enricher/Page1.json"))
        );
        long start = 0;
        long end   = 0;
        try (final MongoJSONStorageConnection connection = storage.openConnection(TEST_COLLECTION)
        ) {
            start = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                connection.addDocument(new MongoDocument(i, i, "doc_" + i, dbNode));
            }
            end = System.currentTimeMillis();
            connection.flush();
            Assert.assertEquals(count, connection.getDocumentsCount());
        } finally {
            if(end == 0) end = System.currentTimeMillis();
            logger.info("Elapsed time: " + (end - start));
        }
    }

    private MongoJSONStorage getStorage() {
        final MongoJSONStorageFactory factory = new MongoJSONStorageFactory();
        return factory.createStorage(
                factory.createConfiguration("localhost:7654:test_load:test_collection")
        );
    }

}
