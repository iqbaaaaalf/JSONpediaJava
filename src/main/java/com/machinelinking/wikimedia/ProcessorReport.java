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

package com.machinelinking.wikimedia;

/**
 * The report of a {@link PageProcessor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ProcessorReport {

    private final long processedPages;
    private final long pagesWithError;
    private final long elapsedTime;

    public ProcessorReport(
            long processedPages, long pagesWithError,
            long elapsedTime
    ) {
        this.processedPages = processedPages;
        this.pagesWithError = pagesWithError;
        this.elapsedTime = elapsedTime;
    }

    public long getProcessedPages() {
        return processedPages;
    }

    public long getPagesWithError() { return pagesWithError; }

    public long getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public String toString() {
        return String.format(
                "Processed pages: %d, pages with errors: %d, elapsed time: %ds (%dms)",
                processedPages,
                pagesWithError,
                elapsedTime / 1000,
                elapsedTime
        );
    }

}
