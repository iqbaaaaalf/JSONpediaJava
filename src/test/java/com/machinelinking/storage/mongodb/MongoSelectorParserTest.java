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
        Assert.assertEquals(
                "criterias: [name eq 'Albert Einstein'], projections: [content, _id, name, content.categories, version]",
                parser.parse("name = Albert Einstein -> content.categories").toString()
        );

        final MongoSelector selector = parser.parse(
                "version <= #0, _id > #10, content.categories.content = Cosmologists, content.sections.title = Biography " +
                "-> _id, name, content"
        );
        Assert.assertEquals(
                "criterias: [version lte 0, _id gt 10, content.categories.content eq 'Cosmologists', content.sections.title eq 'Biography'], " +
                "projections: [content, _id, name, version]",
                selector.toString()
        );

        Assert.assertEquals(
                "criterias: [_id gt 1], projections: [content, _id, name, version]",
                parser.parse("_id > #1 -> _id").toString()
        );
    }

}
