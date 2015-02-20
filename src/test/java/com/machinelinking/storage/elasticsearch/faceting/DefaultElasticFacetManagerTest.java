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
import com.machinelinking.util.JSONUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

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
    public void testSetMappings() {
        final ElasticFacetManagerConfiguration.Property[] p1 = new ElasticFacetManagerConfiguration.Property[] {
                new ElasticFacetManagerConfiguration.Property(
                        "i1",
                        "f1",
                        ElasticFacetManagerConfiguration.PropertyType.string,
                        ElasticFacetManagerConfiguration.Analyzer.not_analyzed
                )
        };
        final Map<String,?> c1 = DefaultElasticFacetManager.setMappings(p1);
        Assert.assertEquals(
                toJSONConfig(c1),
                "{\"f1_index\":{\"properties\":{\"f1\":{\"index\":\"not_analyzed\",\"type\":\"string\"}}}}"
        );

        final ElasticFacetManagerConfiguration.Property[] p2 = new ElasticFacetManagerConfiguration.Property[] {
                new ElasticFacetManagerConfiguration.Property(
                        "i1",
                        "f1",
                        ElasticFacetManagerConfiguration.PropertyType.string,
                        ElasticFacetManagerConfiguration.Analyzer.custom_lowercase
                ),
                new ElasticFacetManagerConfiguration.Property(
                        "i1",
                        "f1",
                        ElasticFacetManagerConfiguration.PropertyType.string,
                        ElasticFacetManagerConfiguration.Analyzer.custom_kstem
                )
        };
        final Map<String,?> c2 = DefaultElasticFacetManager.setMappings(p2);
        Assert.assertEquals(
                toJSONConfig(c2),
                "{\"f1_index\":" +
                "{\"properties\":" +
                "{\"f1\":{" +
                "\"type\":\"multi_field\"," +
                "\"fields\":{" +
                "\"custom_kstem\":{\"analyzer\":\"custom_kstem\",\"type\":\"string\"}," +
                "\"custom_lowercase\":{\"analyzer\":\"custom_lowercase\",\"type\":\"string\"" +
                "}}}}}}"
        );
    }

    @Test
    public void testIndexCreation() throws JSONStorageConnectionException {
        //TODO: missing explicit preconditions (population of TEST_STORAGE_DB)
        final ElasticJSONStorage fromStorage = super.createStorage(TEST_PORT, FROM_STORAGE_DB, FROM_STORAGE_COLLECTION);
        final ElasticJSONStorage facetStorage = super.createStorage(TEST_PORT, FACET_TEST_DB, FACET_TEST_COLLECTION);
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

        Assert.assertEquals(report.getProcessedDocs(), 58);
        Assert.assertEquals(report.getGeneratedFacetDocs(), 1051);
    }

    private String toJSONConfig(Map<String,?> config) {
        return JSONUtils.toJsonNode((Map<String,?>) config.get("mappings")).toString();
    }

}
