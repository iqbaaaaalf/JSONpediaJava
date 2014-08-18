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

import com.machinelinking.storage.JSONStorageConnectionException;
import com.machinelinking.storage.elasticsearch.ElasticJSONStorage;
import com.machinelinking.storage.elasticsearch.ElasticJSONStorageLoaderTest;
import com.machinelinking.storage.elasticsearch.ElasticJSONStorageTestBase;
import com.machinelinking.storage.elasticsearch.ElasticSelector;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Test case for {@link com.machinelinking.storage.elasticsearch.faceting.ElasticFacetManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultElasticFacetManagerTest extends ElasticJSONStorageTestBase {

    public static final String FROM_STORAGE_DB = ElasticJSONStorageLoaderTest.TEST_STORAGE_DB;
    public static final String FROM_STORAGE_COLLECTION = ElasticJSONStorageLoaderTest.TEST_STORAGE_COLLECTION;

    public static final String FACET_TEST_DB = "jsonpedia_test_facet";
    public static final String FACET_TEST_COLLECTION = "en_section";

    @Test
    public void testIndexCreation() throws JSONStorageConnectionException {
        //TODO: missing explicit preconditions (population of TEST_STORAGE_DB)
        final ElasticJSONStorage fromStorage = super.createStorage(FROM_STORAGE_DB, FROM_STORAGE_COLLECTION);
        final ElasticJSONStorage facetStorage = super.createStorage(FACET_TEST_DB, FACET_TEST_COLLECTION);
        facetStorage.deleteCollection();
        final DefaultElasticFacetConfiguration configuration = new DefaultElasticFacetConfiguration(
                new File("conf/faceting.properties"),
                100,
                fromStorage,
                facetStorage
        );

        final ElasticFacetManager manager = new DefaultElasticFacetManager(configuration);
        final ElasticSelector selector = new ElasticSelector();
        final FacetLoadingReport report = manager.loadFacets(selector, new EnrichedEntityFacetConverter());
        Assert.assertEquals(58, report.getProcessedDocs());
        Assert.assertEquals(1051, report.getGeneratedFacetDocs());
    }

}
