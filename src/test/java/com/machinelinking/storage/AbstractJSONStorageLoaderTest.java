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

    private static boolean cleanupDone = false;

    protected abstract JSONStorage getJSONStorage() throws UnknownHostException;

    public void performCleanupOnce() throws UnknownHostException {
        if(!cleanupDone) {
            getJSONStorage().deleteCollection();
            Assert.assertFalse("Collection should not exist any longer.", getJSONStorage().exists());
            cleanupDone = true;
        }
    }

    @Test
    public void testLoaderDump1() throws IOException, SAXException {
        loadLatestPageArticles(1);
    }

    @Test
    public void testLoaderDump2() throws IOException, SAXException {
        loadLatestPageArticles(2);
    }

    @Test
    public void testLoaderDump3() throws IOException, SAXException {
        loadLatestPageArticles(3);
    }


    public void loadLatestPageArticles(int dump) throws IOException, SAXException {
        loadDump(String.format("/dumps/enwiki-latest-pages-articles-p%d.xml.gz", dump), 0);
    }

    public void loadDump(String dump, int expectedIssues) throws IOException, SAXException {
        performCleanupOnce();

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
