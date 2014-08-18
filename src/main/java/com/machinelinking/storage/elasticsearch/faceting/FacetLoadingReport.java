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

/**
 * Report produced by {@link com.machinelinking.storage.elasticsearch.faceting.ElasticFacetManager
 * #loadFacets(com.machinelinking.storage.elasticsearch.ElasticSelector, FacetConverter)} method execution.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FacetLoadingReport {

    private final int processedDocs;
    private final int generatedFacetDocs;

    public FacetLoadingReport(int ingestedDocuments, int generatedFacetDocs) {
        this.processedDocs = ingestedDocuments;
        this.generatedFacetDocs = generatedFacetDocs;
    }

    public int getProcessedDocs() {
        return processedDocs;
    }

    public int getGeneratedFacetDocs() {
        return generatedFacetDocs;
    }

}
