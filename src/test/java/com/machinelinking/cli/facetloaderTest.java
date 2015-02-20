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

package com.machinelinking.cli;

import com.machinelinking.storage.elasticsearch.faceting.DefaultElasticFacetManagerTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Test case for {@link com.machinelinking.cli.facetloader}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class facetloaderTest {

    @Test
    public void testRun() throws IOException {
        final int exitCode = new facetloader().run(
                String.format(
                        "-s localhost:%d:%s:%s -d localhost:%d:%s:%s -l 100 -c conf/faceting.properties",
                        DefaultElasticFacetManagerTest.TEST_PORT,
                        DefaultElasticFacetManagerTest.FROM_STORAGE_DB,
                        DefaultElasticFacetManagerTest.FROM_STORAGE_COLLECTION,
                        DefaultElasticFacetManagerTest.TEST_PORT,
                        DefaultElasticFacetManagerTest.FACET_TEST_DB,
                        DefaultElasticFacetManagerTest.FACET_TEST_COLLECTION
                        ).split("\\s+")
        );

        Assert.assertEquals(0, exitCode);
    }

}
