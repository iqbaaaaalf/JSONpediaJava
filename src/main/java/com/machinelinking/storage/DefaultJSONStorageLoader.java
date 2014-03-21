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
import com.machinelinking.util.FileUtil;
import com.machinelinking.wikimedia.PageProcessor;
import com.machinelinking.wikimedia.ProcessorReport;
import com.machinelinking.wikimedia.WikiDumpMultiThreadProcessor;
import com.machinelinking.wikimedia.WikiPage;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        final JSONStorageConnection conn = storage.openConnection("pages" + threadNumber);
        return new EnrichmentProcessor(
                wikiEnricherFactory.createFullyConfiguredInstance(flags),
                conn
        );
    }

    @Override
    public void finalizeProcessor(EnrichmentProcessor processor) {
        System.out.println(processor.printReport());
    }

    @Override
    public void finalizeProcess(ProcessorReport report) {
        System.out.println(
                String.format(
                        "Total pages: %d, Pages with error: %d, Pages/msec: %f",
                        report.getProcessedPages(),
                        report.getPagesWithError(),
                        report.getProcessedPages() / (float) report.getElapsedTime()
                )
        );
        System.err.println("Unexpected exceptions: " + Arrays.asList(report.getExecutionExceptions()));
    }

    public static class EnrichmentProcessor implements PageProcessor {

        private final WikiEnricher enricher;
        private final JSONStorageConnection connection;
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
                System.err.printf(
                                "Error while processing page [%s], generated JSON:\n ++++\n%s\n++++\n\n\n",
                                pageURL, baos.toString()
                );
                System.err.printf("Page Content:\n++++\n%s\n++++\n", page.getContent());
                e.printStackTrace(System.err);
            } finally {
                processedPages++;
                partialCount++;
                if (partialCount >= LOG_THRESHOLD) {
                    System.out.printf("%s +%d\n", threadId, LOG_THRESHOLD);
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

            final JSONStorageConfiguration storageConfig = jsonStorageFactory.createConfiguration(jsonStorageConfig);
            final JSONStorage storage = jsonStorageFactory.createStorage(storageConfig);
            final DefaultJSONStorageLoader loader = new DefaultJSONStorageLoader(
                    WikiEnricherFactory.getInstance(), flags, storage
            );
            final StorageLoaderReport report = loader.load(
                    prefixURL,
                    FileUtil.openDecompressedInputStream(dumpFile)
            );
            System.out.println(report);
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
