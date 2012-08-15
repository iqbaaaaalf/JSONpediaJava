package com.machinelinking.storage;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class StorageLoaderReport {

    private long processedPages;
    private long pageErrors;
    private long elapsedTime;

    public StorageLoaderReport(long processedPages, long pageErrors, long elapsedTime) {
        this.processedPages = processedPages;
        this.pageErrors = pageErrors;
        this.elapsedTime = elapsedTime;
    }

    public long getProcessedPages() {
        return processedPages;
    }

    public long getPageErrors() {
        return pageErrors;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
}
