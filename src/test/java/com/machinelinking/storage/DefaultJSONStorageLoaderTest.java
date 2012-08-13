package com.machinelinking.storage;

import com.machinelinking.enricher.WikiEnricherFactory;
import junit.framework.Assert;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONStorageLoaderTest {

    private static final WikiEnricherFactory.Flag[] FLAGS = {
            WikiEnricherFactory.Flag.Extractors,
            WikiEnricherFactory.Flag.Splitters,
            WikiEnricherFactory.Flag.Validate
    };

    private static final String FILE_PREFIX = "file://";

    @Test
    public void testLoaderDump1() throws IOException, SAXException {
        loadDump("/enwiki-latest-pages-articles-p1.xml.gz", 0);
    }

    @Test
    public void testLoaderDump2() throws IOException, SAXException {
        loadDump("/enwiki-latest-pages-articles-p2.xml.gz", 0);
    }

    @Test
    public void testLoaderDump3() throws IOException, SAXException {
        loadDump("/enwiki-latest-pages-articles-p3.xml.gz", 0);
    }

    @Ignore
    @Test
    public void testLoaderDump4() throws IOException, SAXException {
        //loadDump("file:///Users/hardest/Downloads/enwiki-latest-pages-articles1.xml-p000000010p000010000.bz2", 0);
        loadDump("file:///Users/hardest/Downloads/enwiki-latest-pages-articles7.xml-p000305002p000464996.bz2", 0);
        //loadDump("file:///Users/hardest/Downloads/enwiki-latest-pages-articles8.xml-p000465001p000665000.bz2", 0);
    }

    public void loadDump(String dump, int expectedIssues) throws IOException, SAXException {
        MongoJSONStorage  jsonStorage = new MongoJSONStorage();
        final DefaultJSONStorageLoader loader = new DefaultJSONStorageLoader(
                WikiEnricherFactory.getInstance(),
                FLAGS,
                jsonStorage
        );

        final StorageLoaderReport report = loader.process(
                new URL("http://en.wikipedia.org/"),
                openResource(dump)
        );

        Assert.assertEquals("Unexpected number of issues.", expectedIssues, report.getPageErrors());
    }

    private InputStream openResource(String resource) throws IOException {
        final int extIndex = resource.lastIndexOf(".");
        final String ext = resource.substring(extIndex + 1);
        final InputStream resourceInputStream;
        if(resource.startsWith(FILE_PREFIX)) {
            resourceInputStream = new FileInputStream(resource.substring(FILE_PREFIX.length()));
        } else {
            resourceInputStream = this.getClass().getResourceAsStream(resource);
        }
        InputStream decompressInputStream;
        switch (ext) {
            case "gz":
                decompressInputStream = new GZIPInputStream(resourceInputStream);
                break;
            case "bz2":
                decompressInputStream = new BZip2CompressorInputStream(resourceInputStream);
                break;
            default:
                throw new IllegalArgumentException("Unsupported extension: " + ext);
        }
        return new BufferedInputStream(decompressInputStream);
    }


}
