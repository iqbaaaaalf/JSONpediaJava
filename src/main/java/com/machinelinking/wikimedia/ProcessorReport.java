package com.machinelinking.wikimedia;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ProcessorReport {

    private final long processedPages;
    private final long elapsedTime;
    private final ExecutionException[] executionExceptions;

    public ProcessorReport(long processedPages, long elapsedTime, ExecutionException[] executionExceptions) {
        this.processedPages = processedPages;
        this.elapsedTime = elapsedTime;
        this.executionExceptions = executionExceptions;
    }

    public long getProcessedPages() {
        return processedPages;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public ExecutionException[] getExecutionExceptions() {
        return executionExceptions;
    }

    @Override
    public String toString() {
        return String.format(
                "Processed pages: %d, elapsed time: %d (ms), exceptions: %s",
                processedPages,
                elapsedTime / 1000,
                Arrays.asList(executionExceptions)
        );
    }
}
