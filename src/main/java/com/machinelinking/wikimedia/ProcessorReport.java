package com.machinelinking.wikimedia;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * The report of a {@link PageProcessor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ProcessorReport {

    private final long processedPages;
    private final long pagesWithError;
    private final long elapsedTime;
    private final ExecutionException[] executionExceptions;

    public ProcessorReport(
            long processedPages, long pagesWithError,
            long elapsedTime,
            ExecutionException[] executionExceptions
    ) {
        this.processedPages = processedPages;
        this.pagesWithError = pagesWithError;
        this.elapsedTime = elapsedTime;
        this.executionExceptions = executionExceptions;
    }

    public long getProcessedPages() {
        return processedPages;
    }

    public long getPagesWithError() { return pagesWithError; }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public ExecutionException[] getExecutionExceptions() {
        return executionExceptions;
    }

    @Override
    public String toString() {
        return String.format(
                "Processed pages: %d, pages with errors: %d, elapsed time: %d (ms), exceptions: %s",
                processedPages,
                pagesWithError,
                elapsedTime / 1000,
                Arrays.asList(executionExceptions)
        );
    }

}
