package com.machinelinking.exporter;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Test case for {@link CSVExporterCLI}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CSVExporterCLITest {

    @Test
    public void testRun() throws IOException {
        final File in  = new File("src/test/resources/dumps/enwiki-latest-pages-articles-p1.xml.gz");
        final File out = File.createTempFile("csv-exporter", ".csv");
        final int exitCode = new CSVExporterCLI().run(
                String.format("--prefix http://en.wikipedia.org --in %s --out %s --threads 1",
                        in.getAbsolutePath(), out.getAbsolutePath())
                        .split(" ")
        );

        Assert.assertEquals(0    , exitCode);
        Assert.assertEquals(11761, FileUtils.readLines(out).size());
    }

}
