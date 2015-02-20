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

import com.machinelinking.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 *  Test case for {@link com.machinelinking.exporter.TextCSVExporter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TextCSVExporterTest {

    @Test
    public void testExport() throws IOException {
        final TextCSVExporter exporter = new TextCSVExporter();
        exporter.setThreads(1);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        exporter.export(
                new URL("http://it.wikipedia.org/"),
                FileUtil.openDecompressedInputStream("/dumps/enwiki-latest-pages-articles-p1.xml.gz"),
                out
        );

        final String expected = IOUtils.toString(this.getClass().getResourceAsStream("text-out.csv"));
        Assert.assertEquals(out.toString(), expected);
    }

}
