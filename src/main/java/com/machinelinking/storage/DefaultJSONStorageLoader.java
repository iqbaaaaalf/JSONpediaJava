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

import com.machinelinking.parser.DocumentSource;
import com.machinelinking.pipeline.Flag;
import com.machinelinking.pipeline.WikiPipeline;
import com.machinelinking.pipeline.WikiPipelineFactory;
import com.machinelinking.serializer.DataEncoder;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.MongoDBDataEncoder;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.util.JSONUtils;
import com.machinelinking.wikimedia.PageProcessor;
import com.machinelinking.wikimedia.ProcessorReport;
import com.machinelinking.wikimedia.WikiDumpMultiThreadProcessor;
import com.machinelinking.wikimedia.WikiPage;
import org.apache.log4j.Logger;
import org.codehaus.jackson.util.TokenBuffer;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

/**
 * Default implementation of {@link JSONStorageLoader}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONStorageLoader
extends WikiDumpMultiThreadProcessor<DefaultJSONStorageLoader.EnrichmentProcessor>
implements JSONStorageLoader {

    private static final int LOG_THRESHOLD = 100;

    private static final Logger logger = Logger.getLogger(DefaultJSONStorageLoader.class);

    private final WikiPipelineFactory wikiEnricherFactory;
    private final Flag[] flags;
    private final JSONStorage storage;

    public DefaultJSONStorageLoader(WikiPipelineFactory factory, Flag[] flags, JSONStorage storage) {
        this.wikiEnricherFactory = factory;
        this.flags               = flags;
        this.storage             = storage;
    }

    @Override
    public WikiPipelineFactory getEnricherFactory() {
        return wikiEnricherFactory;
    }

    @Override
    public JSONStorage getStorage() {
        return storage;
    }

    @Override
    public StorageLoaderReport load(URL pagePrefix, InputStream is) throws IOException, SAXException {
        final ProcessorReport report = process(pagePrefix, is);
        return new StorageLoaderReport(report.getProcessedPages(), report.getPagesWithError(), report.getElapsedTime());
    }

    @Override
    public void initProcess() {
        // Empty.
    }

    @Override
    public EnrichmentProcessor initProcessor(int threadNumber) {
        final JSONStorageConnection conn = storage.openConnection( storage.getConfiguration().getCollection() );
        return new EnrichmentProcessor(
                wikiEnricherFactory.createFullyConfiguredInstance(flags),
                conn
        );
    }

    @Override
    public void finalizeProcessor(EnrichmentProcessor processor) {
        logger.info(processor.printReport());
        processor.connection.close();
    }

    @Override
    public void finalizeProcess(ProcessorReport report) {
        logger.info(
                String.format(
                        "Total pages: %d, Pages with error: %d, Pages/msec: %f",
                        report.getProcessedPages(),
                        report.getPagesWithError(),
                        report.getProcessedPages() / (float) report.getElapsedTime()
                )
        );
    }

    public static class EnrichmentProcessor implements PageProcessor {

        private final WikiPipeline enricher;
        private final JSONStorageConnection connection;
        private int processedPages = 0, errorPages = 0;
        private String threadId;

        private final DataEncoder dataEncoder = new MongoDBDataEncoder();

        public EnrichmentProcessor(
                WikiPipeline wikiEnricher, JSONStorageConnection connection
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

        public long getProcessedPages() {
            return processedPages;
        }

        public long getErrorPages() {
            return errorPages;
        }

        @Override
        public void processPage(String pagePrefix, String threadId, WikiPage page) {
            final TokenBuffer buffer = JSONUtils.createJSONBuffer();
            this.threadId = threadId;
            final String pageURL = pagePrefix + page.getTitle();

            final Serializer serializer;
            try {
                serializer = new JSONSerializer(buffer);
                serializer.setDataEncoder(dataEncoder);

                enricher.enrichEntity(
                        new DocumentSource(
                                new URL(pageURL),
                                new ByteArrayInputStream(page.getContent().getBytes())
                        ),
                        serializer
                );
                connection.addDocument(connection.createDocument(page, buffer));
            } catch (Exception e) {
                e.printStackTrace();
                errorPages++;
                if (logger.isTraceEnabled()) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(">\n>\n>\n>\n");
                    sb.append("Error while processing page [")
                            .append(pageURL)
                            .append("], generated JSON:\n ++++\n")
                            .append(JSONUtils.bufferToJSONString(buffer, true))
                            .append("\n++++\n");
                    sb.append("==== Begin Stack Trace =====");
                    final StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    sb.append(sw.toString());
                    sb.append("==== End   Stack Trace =====");
                    sb.append('\n');
                    sb.append("==== Page Content ====\n++++> ").append(page.getTitle());
                    sb.append(page.getContent());
                    sb.append('\n');
                    sb.append("++++< ").append(page.getTitle());
                    sb.append('\n');
                    sb.append("<\n<\n<\n<\n");
                    logger.trace(sb.toString());
                }
            } finally {
                processedPages++;
                if ((processedPages % LOG_THRESHOLD) == 0) {
                    logger.info(String.format("Processed pages: %s +%d\n", threadId, LOG_THRESHOLD));
                }
            }
        }
    }

}
