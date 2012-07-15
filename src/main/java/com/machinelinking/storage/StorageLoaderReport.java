package com.machinelinking.storage;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class StorageLoaderReport {

    private int processedPages;
    private int pageErrors;
    private long elapsedTime;

    public StorageLoaderReport(int processedPages, int pageErrors, long elapsedTime) {
        this.processedPages = processedPages;
        this.pageErrors = pageErrors;
        this.elapsedTime = elapsedTime;
    }

    public int getProcessedPages() {
        return processedPages;
    }

    public int getPageErrors() {
        return pageErrors;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
}
