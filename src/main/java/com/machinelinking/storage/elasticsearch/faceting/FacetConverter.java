package com.machinelinking.storage.elasticsearch.faceting;

import com.machinelinking.storage.elasticsearch.ElasticDocument;

/**
 * Converts an {@link com.machinelinking.storage.elasticsearch.ElasticDocument}
 * to a set of {@link com.machinelinking.storage.elasticsearch.ElasticDocument}s to be ingested for faceting.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface FacetConverter {

    Iterable<ElasticDocument> convert(ElasticDocument in);

}
