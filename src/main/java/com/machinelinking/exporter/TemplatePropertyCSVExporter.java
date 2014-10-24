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

package com.machinelinking.exporter;

import com.machinelinking.parser.DefaultWikiTextParserHandler;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.util.FileUtil;
import com.machinelinking.wikimedia.PageProcessor;
import com.machinelinking.wikimedia.ProcessorReport;
import com.machinelinking.wikimedia.WikiDumpMultiThreadProcessor;
import com.machinelinking.wikimedia.WikiPage;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Default {@link CSVExporter} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplatePropertyCSVExporter
extends WikiDumpMultiThreadProcessor<TemplatePropertyCSVExporter.TemplatePropertyProcessor>
implements CSVExporter {

    public static final String ANON_PROPERTY_PREFIX = "_anon";

    private static final Logger logger = Logger.getLogger(TemplatePropertyCSVExporter.class);

    private BufferedWriter writer;
    private int threads = 0;

    private long templatesCount          = 0;
    private long propertiesCount         = 0;
    private int maxPropertiesPerTemplate = 0;
    private int propertiesPerTemplate    = 0;

    private long anonId;

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    @Override
    public CSVExporterReport export(URL pagePrefix, InputStream is, OutputStream os) {
        anonId = 0;

        final BufferedInputStream bis =
                is instanceof BufferedInputStream ? (BufferedInputStream) is : new BufferedInputStream(is);
        writer = new BufferedWriter( new OutputStreamWriter(os) );
        try {
            final ProcessorReport report = super.process(
                    pagePrefix,
                    bis,
                    threads <= 0 ? super.getBestNumberOfThreads() : threads
            );
            return new CSVExporterReport(report, templatesCount, propertiesCount, maxPropertiesPerTemplate);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CSVExporterReport export(URL pageURL, File in, File out) throws IOException {
        return export(pageURL, FileUtil.openDecompressedInputStream(in), new FileOutputStream(out));
    }

    @Override
    public void initProcess() {
        // Empty.
    }

    @Override
    public TemplatePropertyProcessor initProcessor(int threadNumber) {
        return new TemplatePropertyProcessor();
    }

    @Override
    public void finalizeProcessor(TemplatePropertyProcessor processor) {
        // Empty.
    }

    @Override
    public void finalizeProcess(ProcessorReport report) {
        try {
            writer.close();
        } catch (IOException ioe) {
            throw new RuntimeException("Error while closing output writer.", ioe);
        }
    }

    private void writeLine(String line) {
        synchronized (writer) {
            try {
                writer.write(line);
            } catch (IOException ioe) {
                throw new RuntimeException("Error while writing line.", ioe);
            }
        }
    }

    public class TemplatePropertyProcessor extends DefaultWikiTextParserHandler implements PageProcessor {

        private final WikiTextParser parser = new WikiTextParser(this);

        private boolean insideTemplate = false;
        private boolean nextIsValue    = false;

        private int    pageId;
        private String pageTitle;
        private String pageURL;
        private String template;
        private String property;

        private long processedPages;
        private long errorPages;

        @Override
        public void processPage(String pagePrefix, String threadId, WikiPage page) {
            this.pageId    = page.getId();
            this.pageTitle = page.getTitle();
            this.pageURL   = pagePrefix + pageTitle;
            final URL pageURL;
            try {
                pageURL = new URL(this.pageURL);
            } catch (MalformedURLException murle) {
                throw new RuntimeException(murle);
            }
            try {
                parser.parse( new DocumentSource(pageURL, page.getContent()));
            } catch (Exception e) {
                errorPages++;
                logger.error("Error while parsing page " + pageURL, e);
            } finally {
                processedPages++;
            }
        }

        @Override
        public long getProcessedPages() {
            return processedPages;
        }

        @Override
        public long getErrorPages() {
            return errorPages;
        }

        @Override
        public void beginTemplate(TemplateName name) {
            insideTemplate = true;
            template = cleanString(name.plain.trim());
            templatesCount++;
            if(propertiesPerTemplate > maxPropertiesPerTemplate) {
                maxPropertiesPerTemplate = propertiesPerTemplate;
            }
            propertiesPerTemplate = 0;
        }

        @Override
        public void parameter(String param) {
            property = param == null ? getNextAnonProperty() : param.trim();
            nextIsValue = true;
            propertiesCount++;
            propertiesPerTemplate++;
        }

        @Override
        public void text(String content) {
            if(insideTemplate && nextIsValue) {
                print( cleanString(content.trim()) );
            }
        }

        @Override
        public void beginLink(URL url) {
            if(insideTemplate && nextIsValue) {
                print(String.format("[[%s]]", url));
            }
        }

        @Override
        public void beginReference(String label) {
            print(String.format("[%s]", label));
        }

        @Override
        public void endTemplate(TemplateName name) {
            insideTemplate = false;
        }

        private String getNextAnonProperty() {
            return ANON_PROPERTY_PREFIX + anonId++;
        }

        private String cleanString(String in) {
            return in.replace('\t', ' ').replace('\n', ' ');
        }

        private void print(String value) {
            writeLine(
                    String.format(
                            "%d\t%s\t%s\t%s\t%s\n",
                            pageId, pageTitle, template, property, value
                    )
            );
        }

    }

}
