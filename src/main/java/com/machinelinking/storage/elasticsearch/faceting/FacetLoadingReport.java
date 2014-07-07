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
