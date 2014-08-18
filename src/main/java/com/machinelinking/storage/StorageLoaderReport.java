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

package com.machinelinking.storage;

/**
 * Report produced by a {@link JSONStorageLoader}.
 *
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

    @Override
    public String toString() {
        return String.format(
                "Processed pages: %d, pages with error: %d, elapsed time: %d ms, pages/ms: %f, errors/total pages: %f",
                processedPages, pageErrors, elapsedTime,
                (processedPages / (float)elapsedTime),
                (pageErrors / (float) processedPages)
        );
    }

}
