package com.machinelinking.storage;

import com.machinelinking.enricher.Flag;
import com.machinelinking.enricher.WikiEnricher;
import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.serializer.DataEncoder;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.MongoDBDataEncoder;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.wikimedia.BufferedWikiPageHandler;
import com.machinelinking.wikimedia.WikiDumpParser;
import com.machinelinking.wikimedia.WikiPage;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONStorageLoader implements JSONStorageLoader {

    public static final int MIN_NUM_OF_THREADS = 2;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final WikiEnricherFactory wikiEnricherFactory;

    private final Flag[] flags;

    private final JSONStorage storage;

    private BufferedWikiPageHandler bufferedHandler;

    public DefaultJSONStorageLoader(WikiEnricherFactory factory, Flag[] flags, JSONStorage storage) {
        this.wikiEnricherFactory = factory;
        this.flags               = flags;
        this.storage             = storage;
    }

    @Override
    public WikiEnricherFactory getEnricherFactory() {
        return wikiEnricherFactory;
    }

    @Override
    public JSONStorage getStorage() {
        return storage;
    }

    @Override
    public StorageLoaderReport process(URL pagePrefix, InputStream is) throws IOException, SAXException {
        return process(pagePrefix, is, getBestNumberOfThreads());
    }

    public StorageLoaderReport process(URL pagePrefix, InputStream is, int threads) throws IOException, SAXException {
        if(threads <= 0) throw new IllegalArgumentException("Invalid number of threads: " + threads);

        System.out.println("Processing with " + threads + " threads");
        WikiDumpParser dumpParser = new WikiDumpParser();
        bufferedHandler = new BufferedWikiPageHandler();
        final List<Future> futures               = new ArrayList<>();
        final List<EnrichmentRunnable> runnables = new ArrayList<>();
        for(int t = 0; t < threads; t++) {
            final JSONStorageConnection conn = storage.openConnection("pages" + t);
            final EnrichmentRunnable runnable = new EnrichmentRunnable(
                    "t" + t,
                    pagePrefix.toExternalForm(),
                    wikiEnricherFactory.createFullyConfiguredInstance(flags),
                    conn
            );
            runnables.add(runnable);
            final Future future = executorService.submit(runnable);
            futures.add(future);
        }

        final long startTime = System.currentTimeMillis();
        dumpParser.parse(bufferedHandler, is);
        try {
            for(Future future : futures) {
                future.get();
            }
        } catch(Exception e) {
            throw new RuntimeException("Error while waiting operation completion.", e);
        } finally {
            System.out.println("Closing connection...");
            storage.close();
            System.out.println("Done.");
            final long endTime = System.currentTimeMillis();
            final long elapsedTime = endTime - startTime;

            long processedPages = 0;
            long pagesWithError = 0;
            for(EnrichmentRunnable runnable : runnables) {
                System.out.println(runnable.printReport());
                processedPages += runnable.getProcessedPages();
                pagesWithError += runnable.getErrorPages();
            }
            System.out.println(
                    String.format(
                            "Elapsed time: %d msec. Total pages: %d, Pages/msec: %f",
                            elapsedTime, processedPages, processedPages / (float) elapsedTime
                    )
            );
            return new StorageLoaderReport(processedPages, pagesWithError, elapsedTime);
        }
    }

    private int getBestNumberOfThreads() {
        final int candidate = Runtime.getRuntime().availableProcessors();
        return candidate < MIN_NUM_OF_THREADS ? MIN_NUM_OF_THREADS : candidate;
    }

    private class EnrichmentRunnable implements Runnable {

        private final String threadId;
        private final String pagePrefix;
        private final WikiEnricher enricher;
        private final JSONStorageConnection connection;

        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        private int processedPages = 0, errorPages = 0;

        private EnrichmentRunnable(
                String threadId, String pagePrefix, WikiEnricher wikiEnricher, JSONStorageConnection connection
        ) {
            this.threadId   = threadId;
            this.pagePrefix = pagePrefix;
            this.enricher   = wikiEnricher;
            this.connection = connection;
        }

        protected String printReport() {
            return String.format(
                    "Thread %s completed. Processed pages: %d, Errors: %d",
                    threadId, processedPages, errorPages
            );
        }

        protected int getProcessedPages() {
            return processedPages;
        }

        protected int getErrorPages() {
            return errorPages;
        }

        @Override
        public void run() {
            WikiPage page;
            String pageURL = null;
            int partialCount = 0;
            Serializer serializer;
            final DataEncoder dataEncoder = new MongoDBDataEncoder();
            while((page = bufferedHandler.getPage(true)) != BufferedWikiPageHandler.EOQ) {
                baos.reset();
                try {
                    serializer = new JSONSerializer(baos);
                    serializer.setDataEncoder(dataEncoder);
                    pageURL = pagePrefix + page.getId();
                    enricher.enrichEntity(
                            new DocumentSource(
                                    new URL(pageURL),
                                    new ByteArrayInputStream( page.getContent().getBytes() )
                            ),
                            serializer
                    );
                    final DBObject dbNode = (DBObject) JSON.parse(baos.toString()); // TODO: avoid it.
                    connection.addDocument(new DBObjectDocument(page.getId(), dbNode));
                } catch (Exception e) {
                    errorPages++;
                    System.out.println(
                            String.format(
                                    "Error while processing page [%s], generated JSON: <%s>",
                                    pageURL, baos.toString()
                            )
                    );
                    System.out.println(
                            "Page Content:\n========================================\n" +
                            page.getContent() +
                            "\n========================================\n"
                    );
                    e.printStackTrace();
                } finally {
                    processedPages++;
                    partialCount++;
                    if(partialCount >= 100) {
                        System.out.print(threadId);
                        partialCount = 0;
                    }
                }
            }

        }
    }

}
