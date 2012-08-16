package com.machinelinking.exporter;

import com.machinelinking.parser.DefaultWikiTextParserHandler;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.wikimedia.PageProcessor;
import com.machinelinking.wikimedia.ProcessorReport;
import com.machinelinking.wikimedia.WikiDumpMultiThreadProcessor;
import com.machinelinking.wikimedia.WikiPage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultCSVExporter extends WikiDumpMultiThreadProcessor<DefaultCSVExporter.TemplatePropertyProcessor>
implements CSVExporter {

    private BufferedWriter writer;

    @Override
    public CSVExporterReport export(URL pagePrefix, InputStream is, OutputStream os) {
        writer = new BufferedWriter( new OutputStreamWriter(os) );
        try {
            final ProcessorReport report = super.process(pagePrefix, is);
            System.out.println(report);
            return null;
            //return new CSVExporterReport(report.);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

//        private boolean insideTemplate = false;
        private boolean nextIsValue = false;

        private String pageID;
        private String pageURL;
        private String template;
        private String property;

        @Override
        public void processPage(String pagePrefix, String threadId, WikiPage page) {
            this.pageID = page.getId();
            this.pageURL = pagePrefix + page.getId();
            final URL pageURL;
            try {
                pageURL = new URL(this.pageURL);
            } catch (MalformedURLException murle) {
                throw new RuntimeException(murle);
            }
            try {
                parser.parse( new DocumentSource(pageURL, page.getContent()));
            } catch (Exception e) {
                throw new RuntimeException("Error while parsing page " + pageURL, e);
            }
        }

        @Override
        public void beginTemplate(String name) {
            template = cleanString(name.trim());
        }

        @Override
        public void templateParameterName(String param) {
            property = param.trim();
            nextIsValue = true;
        }

        @Override
        public void text(String content) {
            if(nextIsValue) {
                writeLine(
                        String.format(
                                "%s\t%d\t%s\t%s\t%s\n",
                                pageID, 1, template, property, cleanString(content.trim())
                        )
                );
                nextIsValue = false;
            }
        }

        private String cleanString(String in) {
            return in.replace('\t', ' ').replace('\n', ' ');
        }

    }

}
