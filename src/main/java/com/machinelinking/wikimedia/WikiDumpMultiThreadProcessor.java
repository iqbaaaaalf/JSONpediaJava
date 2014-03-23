package com.machinelinking.wikimedia;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A multi-thread executor for {@link PageProcessor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class WikiDumpMultiThreadProcessor <P extends PageProcessor> {

    public static final int MIN_NUM_OF_THREADS = 2;

    private static final Logger logger = Logger.getLogger(WikiDumpMultiThreadProcessor.class);

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private BufferedWikiPageHandler bufferedHandler;

    private boolean stopAtFirstError = true;

    private URL pagePrefix;

    public WikiDumpMultiThreadProcessor() {
    }

    public abstract void initProcess();

    public abstract P initProcessor(int threadNumber);

    public abstract void finalizeProcessor(P processor);

    public abstract void finalizeProcess(ProcessorReport report);

    public URL getPagePrefix() {
        return pagePrefix;
    }

    public boolean isStopAtFirstError() {
        return stopAtFirstError;
    }

    public void setStopAtFirstError(boolean stopAtFirstError) {
        this.stopAtFirstError = stopAtFirstError;
    }

    public ProcessorReport process(URL pagePrefix, InputStream is) throws IOException, SAXException {
        return process(pagePrefix, is, getBestNumberOfThreads());
    }

    public ProcessorReport process(URL pagePrefix, InputStream is, int threads) throws IOException, SAXException {
        if(pagePrefix == null) throw new NullPointerException();
        if (threads <= 0) throw new IllegalArgumentException("Invalid number of threads: " + threads);
        this.pagePrefix = pagePrefix;

        logger.info("Starting processing with " + threads + " threads");

        initProcess();

        WikiDumpParser dumpParser = new WikiDumpParser();
        bufferedHandler = new BufferedWikiPageHandler();

        final List<Future> futures = new ArrayList<>();
        final List<RunnableProcessor> runnableProcessors = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            final P processor = initProcessor(t);
            final WikiDumpRunnable runnable = new WikiDumpRunnable(
                    "thread" + t,
                    pagePrefix.toExternalForm(),
                    bufferedHandler,
                    processor
            );
            runnableProcessors.add( new RunnableProcessor(runnable, processor) );
            final Future future = executorService.submit(runnable);
            futures.add(future);
        }

        final List<ExecutionException> executionExceptions = new ArrayList<>();
        final long startTime = System.currentTimeMillis();
        final long endTime;
        dumpParser.parse(bufferedHandler, is);
        long totalProcessedPages = 0;
        long totalErrorPages = 0;
        try {
            for (Future future : futures) {
                try {
                    future.get();
                } catch (ExecutionException ee) {
                    if(stopAtFirstError) {
                        throw new RuntimeException("Error while executing thread.", ee);
                    } else {
                        executionExceptions.add(ee);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while waiting operation completion.", e);
        } finally {
            logger.info("Process closed.");
            endTime = System.currentTimeMillis();
            for (RunnableProcessor runnableProcessor : runnableProcessors) {
                totalProcessedPages += runnableProcessor.processor.getProcessedPages();
                totalErrorPages += runnableProcessor.processor.getErrorPages();
                finalizeProcessor(runnableProcessor.processor);
            }
        }
        final long elapsedTime = endTime - startTime;
        final ProcessorReport report = new ProcessorReport(
                totalProcessedPages,
                totalErrorPages,
                elapsedTime,
                executionExceptions.toArray(new ExecutionException[executionExceptions.size()])
        );
        finalizeProcess(report);
        return report;
    }

    protected int getBestNumberOfThreads() {
        final int candidate = Runtime.getRuntime().availableProcessors();
        return candidate < MIN_NUM_OF_THREADS ? MIN_NUM_OF_THREADS : candidate;
    }

    class RunnableProcessor {
        final WikiDumpRunnable runnable;
        final P processor;

        RunnableProcessor(WikiDumpRunnable runnable, P processor) {
            this.runnable = runnable;
            this.processor = processor;
        }
    }

}
