package com.machinelinking.storage;

import com.machinelinking.enricher.Flag;
import com.machinelinking.enricher.WikiEnricher;
import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.serializer.DataEncoder;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.MongoDBDataEncoder;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.util.FileUtil;
import com.machinelinking.util.JSONUtils;
import com.machinelinking.wikimedia.PageProcessor;
import com.machinelinking.wikimedia.ProcessorReport;
import com.machinelinking.wikimedia.WikiDumpMultiThreadProcessor;
import com.machinelinking.wikimedia.WikiPage;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.util.TokenBuffer;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

/**
 * Default implementation of {@link JSONStorageLoader}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONStorageLoader
extends WikiDumpMultiThreadProcessor<DefaultJSONStorageLoader.EnrichmentProcessor>
implements JSONStorageLoader {

    private static final int LOG_THRESHOLD = 1000;

    private static final Logger logger = Logger.getLogger(DefaultJSONStorageLoader.class);

    private final WikiEnricherFactory wikiEnricherFactory;
    private final Flag[] flags;
    private final JSONStorage storage;

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

        private final WikiEnricher enricher;
        private final JSONStorageConnection connection;
        private int processedPages = 0, errorPages = 0;
        private int partialCount = 0;
        private String threadId;

        private final DataEncoder dataEncoder = new MongoDBDataEncoder();

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
                partialCount++;
                if (partialCount >= LOG_THRESHOLD) {
                    logger.debug(String.format("%s +%d\n", threadId, LOG_THRESHOLD));
                    partialCount = 0;
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: $0 <config-file> <dump>");
            System.exit(1);
        }

        try {
            final File configFile = check(args[0]);
            final File dumpFile = check(args[1]);
            final Properties properties = new Properties();
            properties.load(FileUtils.openInputStream(configFile));

            final Flag[] flags = WikiEnricherFactory.getInstance().toFlags(
                    getPropertyOrFail(
                            properties,
                            "loader.flags",
                            "valid flags: " + Arrays.toString(WikiEnricherFactory.getInstance().getDefinedFlags())
                    )
            );
            final JSONStorageFactory jsonStorageFactory = loadJSONStorageFactory(
                    getPropertyOrFail(
                            properties,
                            "loader.storage.factory",
                            null
                    )
            );
            final String jsonStorageConfig = getPropertyOrFail(
                    properties,
                    "loader.storage.config",
                    null
            );
            final URL prefixURL = readURL(
                    getPropertyOrFail(
                            properties,
                            "loader.prefix.url",
                            "expected a valid URL prefix like: http://en.wikipedia.org/"
                    ),
                    "loader.prefix.url"
            );

            final DefaultJSONStorageLoader[] loader = new DefaultJSONStorageLoader[1];
            final boolean[] finalReportProduced = new boolean[]{false};
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    if(!finalReportProduced[0] && loader[0] != null) {
                        System.err.println("Process interrupted. Partial loading report: " + loader[0].createReport());
                    }
                    System.err.println("Shutting down.");
                }
            }));

            final JSONStorageConfiguration storageConfig = jsonStorageFactory.createConfiguration(jsonStorageConfig);
            try (final JSONStorage storage = jsonStorageFactory.createStorage(storageConfig)) {
                loader[0] = new DefaultJSONStorageLoader(
                        WikiEnricherFactory.getInstance(), flags, storage
                );

                final StorageLoaderReport report = loader[0].load(
                        prefixURL,
                        FileUtil.openDecompressedInputStream(dumpFile)
                );
                System.err.println("Loading report: " + report);
                finalReportProduced[0] = true;
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static File check(String file) {
        final File f = new File(file);
        if(!f.exists()) throw new IllegalArgumentException(String.format("Invalid file: %s", f.getAbsolutePath()));
        return f;
    }

    private static URL readURL(String url, String desc) {
        try {
            return new URL(url);
        } catch (MalformedURLException murle) {
            throw new IllegalStateException(String.format("Invalid URL specified for [%s]", desc), murle);
        }
    }

    private static JSONStorageFactory loadJSONStorageFactory(String className) {
        try {
            return (JSONStorageFactory) DefaultJSONStorageLoader.class.getClassLoader()
                    .loadClass(className).newInstance();
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException( String.format("Invalid class name: %s .", className) );
        } catch (Exception e) {
            throw new IllegalArgumentException( String.format("Error while loading class: %s .", className), e);
        }
    }

    private static String getPropertyOrFail(Properties properties, String property, String errMsg) {
        final String value = properties.getProperty(property);
        if(value == null) throw new IllegalArgumentException(
                String.format("Invalid properties file: must define property [%s] - %s.", property, errMsg)
        );
        return value;
    }

}
