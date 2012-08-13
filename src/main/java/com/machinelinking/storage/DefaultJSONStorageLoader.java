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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONStorageLoader implements JSONStorageLoader {

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
        WikiDumpParser dumpParser = new WikiDumpParser();
        bufferedHandler = new BufferedWikiPageHandler();
        final JSONStorageConnection conn1 = storage.openConnection("pages1");
        final JSONStorageConnection conn2 = storage.openConnection("pages2");
        final EnrichmentRunnable runnable1 = new EnrichmentRunnable(
                "t1",
                pagePrefix.toExternalForm(),
                wikiEnricherFactory.createFullyConfiguredInstance(flags),
                conn1
        );
        final EnrichmentRunnable runnable2 = new EnrichmentRunnable(
                "t2",
                pagePrefix.toExternalForm(),
                wikiEnricherFactory.createFullyConfiguredInstance(flags),
                conn2
        );
        final long startTime = System.currentTimeMillis();
        Future future1 = executorService.submit(runnable1);
        Future future2 = executorService.submit(runnable2);
        dumpParser.parse(bufferedHandler, is);
        try {
            future1.get();
            future2.get();
        } catch(Exception e) {
            throw new RuntimeException("Error while waiting operation completion.", e);
        } finally {
            System.out.println("Closing connection...");
            storage.close();
            System.out.println("Done.");
            final long endTime = System.currentTimeMillis();
            final long elapsedTime = endTime - startTime;
            System.out.println(runnable1.printReport());
            System.out.println(runnable2.printReport());
            final int totalPages = runnable1.getProcessedPages() + runnable2.getProcessedPages();
            final int errorPages = runnable1.getErrorPages()     + runnable2.getErrorPages();
            System.out.println(
                    String.format(
                            "Elapsed time: %d msec. Total pages: %d, Pages/msec: %f",
                            elapsedTime, totalPages, totalPages / (float) elapsedTime
                    )
            );
            return new StorageLoaderReport(totalPages, errorPages, elapsedTime);
        }
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
