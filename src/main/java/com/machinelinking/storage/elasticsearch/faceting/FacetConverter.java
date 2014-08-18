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
