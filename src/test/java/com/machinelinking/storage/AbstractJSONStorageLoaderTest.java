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

import com.machinelinking.pipeline.Flag;
import com.machinelinking.pipeline.WikiPipelineFactory;
import com.machinelinking.util.FileUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
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
            WikiPipelineFactory.Extractors,
            WikiPipelineFactory.Splitters,
            WikiPipelineFactory.Validate
    };

    private static boolean cleanupDone = false;

    protected abstract JSONStorage getJSONStorage() throws UnknownHostException;

    public void performCleanupOnce() throws UnknownHostException {
        if(!cleanupDone) {
            getJSONStorage().deleteCollection();
            Assert.assertFalse(getJSONStorage().exists(), "Collection should not exist any longer.");
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
                WikiPipelineFactory.getInstance(),
                FLAGS,
                getJSONStorage()
        );

        final StorageLoaderReport report = loader.load(
                new URL("http://en.wikipedia.org/"),
                FileUtil.openDecompressedInputStream(dump)
        );

        Assert.assertEquals(report.getPageErrors(), expectedIssues, "Unexpected number of issues.");
    }

}
