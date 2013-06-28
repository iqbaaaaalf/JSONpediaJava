package com.machinelinking.storage;

import com.machinelinking.enricher.Flag;
import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.util.FileUtil;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONStorageLoaderTest {

    private static final Flag[] FLAGS = {
            WikiEnricherFactory.Extractors,
            WikiEnricherFactory.Splitters,
            WikiEnricherFactory.Validate
    };

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

    @Ignore
    @Test
    public void testLoaderDump4() throws IOException, SAXException {
        //loadDump("file:///Users/hardest/Downloads/enwiki-latest-pages-articles1.xml-p000000010p000010000.bz2", 0);
        loadDump("file:///Users/hardest/Downloads/enwiki-latest-pages-articles7.xml-p000305002p000464996.bz2", 0);
        //loadDump("file:///Users/hardest/Downloads/enwiki-latest-pages-articles8.xml-p000465001p000665000.bz2", 0);
    }

    public void loadDump(String dump, int expectedIssues) throws IOException, SAXException {
        MongoJSONStorage  jsonStorage = new MongoJSONStorage(
                new MongoJSONStorageConfiguration("127.0.0.1", 7654, "jsonpedia")
        );
        final DefaultJSONStorageLoader loader = new DefaultJSONStorageLoader(
                WikiEnricherFactory.getInstance(),
                FLAGS,
                jsonStorage
        );

        final StorageLoaderReport report = loader.load(
                new URL("http://en.wikipedia.org/"),
                FileUtil.openDecompressedInputStream(dump)
        );

        Assert.assertEquals("Unexpected number of issues.", expectedIssues, report.getPageErrors());
    }

}
