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

package com.machinelinking.storage.elasticsearch.faceting;

import com.machinelinking.storage.SelectorParserException;
import com.machinelinking.storage.elasticsearch.ElasticSelectorParser;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for {@link com.machinelinking.storage.elasticsearch.ElasticSelectorParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticSelectorParserTest {

    @Test
    public void testParse() throws SelectorParserException {
        Assert.assertEquals(ElasticSelectorParser.getInstance().parse("value").toString(), "_any eq 'value'");
        Assert.assertEquals(ElasticSelectorParser.getInstance().parse("name:value").toString(), "name eq 'value'");
        Assert.assertEquals(ElasticSelectorParser.getInstance().parse("name:\"value\"").toString(), "name eq 'value'");
        Assert.assertEquals(ElasticSelectorParser.getInstance().parse("name:\"v1 AND v2\"").toString(), "name eq 'v1 AND v2'");
        Assert.assertEquals(ElasticSelectorParser.getInstance().parse("name:\"v1 OR v2\"").toString(), "name eq 'v1 OR v2'");
    }

}
