package com.machinelinking.storage;

import com.machinelinking.enricher.Flag;
import com.machinelinking.enricher.WikiEnricher;
import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.serializer.DataEncoder;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.MongoDBDataEncoder;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.storage.mongodb.MongoDocument;
import com.machinelinking.wikimedia.PageProcessor;
import com.machinelinking.wikimedia.ProcessorReport;
import com.machinelinking.wikimedia.WikiDumpMultiThreadProcessor;
import com.machinelinking.wikimedia.WikiPage;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

/**
 * Default implementation of {@link JSONStorageLoader}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONStorageLoader
extends WikiDumpMultiThreadProcessor<DefaultJSONStorageLoader.EnrichmentProcessor>
implements JSONStorageLoader {

    private final WikiEnricherFactory wikiEnricherFactory;
    private final Flag[] flags;
    private final JSONStorage storage;

    private long pagesWithError = 0;

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
    public StorageLoaderReport load(URL pagePrefix, InputStream is) throws IOException, SAXException {
        final ProcessorReport report = process(pagePrefix, is);
        return new StorageLoaderReport(report.getProcessedPages(), pagesWithError, report.getElapsedTime());
    }

    @Override
    public void initProcess() {
        // Empty.
    }

    @Override
    public EnrichmentProcessor initProcessor(int threadNumber) {
        final JSONStorageConnection conn = storage.openConnection("pages" + threadNumber);
        return new EnrichmentProcessor(
                wikiEnricherFactory.createFullyConfiguredInstance(flags),
                conn
        );
    }

    @Override
    public void finalizeProcessor(EnrichmentProcessor processor) {
        pagesWithError += processor.getErrorPages();
        System.out.println(processor.printReport());
    }

    @Override
    public void finalizeProcess(ProcessorReport report) {
        System.out.println(
                String.format(
                        "Total pages: %d, Pages with error: %d, Pages/msec: %f",
                        report.getProcessedPages(),
                        pagesWithError,
                        report.getProcessedPages() / (float) report.getElapsedTime()
                )
        );
        System.out.println("Unexpected exceptions: " + Arrays.asList(report.getExecutionExceptions()));
    }

    public static class EnrichmentProcessor implements PageProcessor {

        private final WikiEnricher enricher;
        private final JSONStorageConnection connection;
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private int processedPages = 0, errorPages = 0;
        private int partialCount = 0;
        private String threadId;

        final DataEncoder dataEncoder = new MongoDBDataEncoder();

        public EnrichmentProcessor(
                WikiEnricher wikiEnricher, JSONStorageConnection connection
        ) {
            super();
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
        public void processPage(String pagePrefix, String threadId, WikiPage page) {
            baos.reset();
            this.threadId = threadId;
            final String pageURL = pagePrefix + page.getTitle();

            final Serializer serializer;
            try {
                serializer = new JSONSerializer(baos);
                serializer.setDataEncoder(dataEncoder);

                enricher.enrichEntity(
                        new DocumentSource(
                                new URL(pageURL),
                                new ByteArrayInputStream(page.getContent().getBytes())
                        ),
                        serializer
                );
                final DBObject dbNode = (DBObject) JSON.parse(baos.toString()); // TODO: avoid it.
                connection.addDocument(new MongoDocument(page.getTitle(), page.getId(), page.getRevId(), dbNode));
            } catch (Exception e) {
                errorPages++;
                System.out.println(
                        String.format(
                                "Error while processing page [%s], generated JSON: <%s>",
                                pageURL, baos.toString()
                        )
                );
                System.out.println(
                        "Page Content:" +
                                "\n========================================\n" +
                                page.getContent() +
                                "\n========================================\n"
                );
                e.printStackTrace();
            } finally {
                processedPages++;
                partialCount++;
                if (partialCount >= 100) {
                    System.out.print(threadId);
                    partialCount = 0;
                }
            }
        }
    }

}
