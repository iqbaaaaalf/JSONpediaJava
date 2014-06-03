package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.Criteria;
import com.machinelinking.storage.JSONStorageConnection;
import com.machinelinking.storage.JSONStorageConnectionException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import java.io.IOException;

/**
 * Test case for {@link com.machinelinking.storage.mongodb.MongoJSONStorage}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageTest {

    public static String TEST_DB = "test_db";
    public static String TEST_COLLECTION = "test_collection";

    private static final String MAP_FUNC = "function() {  ocs = this.content.templates.occurrences; for(template in ocs) { emit(template, ocs[template]); } }";
    private static final String RED_FUNC = "function(key, values) { return Array.sum(values) }";

    private static Logger logger = Logger.getLogger(MongoJSONStorageTest.class);

    // 10000 items: 46970 ms
    @Test
    public void testLoad() throws IOException {
        loadAndCheck(100);
    }

    @Test
    public void testQuery() throws IOException, JSONStorageConnectionException {
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

    @Test
    public void testMapReduce() {
        final MongoJSONStorage storage = getStorage();
        final MongoJSONStorageConnection connection = storage.openConnection(TEST_COLLECTION);
        final JsonNode result = connection.processMapReduce(new BasicDBObject(), MAP_FUNC, RED_FUNC, 0);
        Assert.assertTrue(result.isArray());
        Assert.assertTrue(result.size() > 50);
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
                factory.createConfiguration(String.format("localhost:7654:%s:%s", TEST_DB, TEST_COLLECTION))
        );
    }

}
