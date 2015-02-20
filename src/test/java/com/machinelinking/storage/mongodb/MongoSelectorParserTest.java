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

package com.machinelinking.storage.mongodb;

import org.testng.Assert;
import org.testng.annotations.Test;

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
                parser.parse("name = Albert Einstein -> content.categories").toString(),
                "criterias: [name eq 'Albert Einstein'], projections: [content, _id, name, content.categories, version]"

        );

        final MongoSelector selector = parser.parse(
                "version <= #0, _id > #10, content.categories.content = Cosmologists, content.sections.title = Biography " +
                "-> _id, name, content"
        );
        Assert.assertEquals(
                selector.toString(),
                "criterias: [version lte 0, _id gt 10, content.categories.content eq 'Cosmologists', content.sections.title eq 'Biography'], " +
                "projections: [content, _id, name, version]"
        );

        Assert.assertEquals(
                parser.parse("_id > #1 -> _id").toString(),
                "criterias: [_id gt 1], projections: [content, _id, name, version]"
        );
    }

}
