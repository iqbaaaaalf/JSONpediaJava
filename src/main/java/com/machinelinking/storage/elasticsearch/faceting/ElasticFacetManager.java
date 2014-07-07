package com.machinelinking.storage.elasticsearch.faceting;

import com.machinelinking.storage.JSONStorageConnectionException;
import com.machinelinking.storage.elasticsearch.ElasticSelector;

/**
 * Defines a Facet Manager for <i>Elastic Search</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface ElasticFacetManager {

    ElasticFacetManagerConfiguration getConfiguration();

    FacetLoadingReport loadFacets(ElasticSelector selector, FacetConverter converter) throws JSONStorageConnectionException;

}
