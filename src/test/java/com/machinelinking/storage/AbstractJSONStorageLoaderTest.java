package com.machinelinking.storage;

import com.machinelinking.enricher.Flag;
import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.util.FileUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Abstract loader test case.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class AbstractJSONStorageLoaderTest {

    private static final Flag[] FLAGS = {
            WikiEnricherFactory.Extractors,
            WikiEnricherFactory.Splitters,
            WikiEnricherFactory.Validate
    };

    protected abstract JSONStorage getJSONStorage() throws UnknownHostException;

    @Test
    public void testLoaderDump1() throws IOException, SAXException {
        loadDump("/dumps/enwiki-latest-pages-articles-p1.xml.gz", 0);
    }

    @Test
    public void testLoaderDump2() throws IOException, SAXException {
        loadDump("/dumps/enwiki-latest-pages-articles-p2.xml.gz", 0);
    }

    @Test
    public void testLoaderDump3() throws IOException, SAXException {
        loadDump("/dumps/enwiki-latest-pages-articles-p3.xml.gz", 0);
    }

    public void loadDump(String dump, int expectedIssues) throws IOException, SAXException {

        final DefaultJSONStorageLoader loader = new DefaultJSONStorageLoader(
                WikiEnricherFactory.getInstance(),
                FLAGS,
                getJSONStorage()
        );

        final StorageLoaderReport report = loader.load(
                new URL("http://en.wikipedia.org/"),
                FileUtil.openDecompressedInputStream(dump)
        );

        Assert.assertEquals("Unexpected number of issues.", expectedIssues, report.getPageErrors());
    }

}
