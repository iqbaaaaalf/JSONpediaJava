package com.machinelinking.storage.mongodb;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Test case for {@link com.machinelinking.storage.mongodb.MongoSelector}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoSelectorParserTest {

    @Test
    public void testParser() {
        final MongoSelectorParser parser = MongoSelectorParser.getInstance();
        final MongoSelector selector = parser.parse(
                "version <= 0, _id > 10, content.categories.content = Cosmologists, content.sections.title = Biography " +
                "-> _id, name, content"
        );
        Assert.assertEquals(
                "criterias: [version lte 0, _id gt 10, content.categories.content eq Cosmologists, content.sections.title eq Biography], " +
                "projections: [_id, name, content]",
                selector.toString()
        );
    }

}
