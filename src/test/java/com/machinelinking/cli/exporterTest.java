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

package com.machinelinking.cli;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Test case for {@link com.machinelinking.cli.exporter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class exporterTest {

    @Test
    public void testRun() throws IOException {
        final File in  = new File("src/test/resources/dumps/enwiki-latest-pages-articles-p1.xml.gz");
        final File out = File.createTempFile("csv-exporter", ".csv");
        final int exitCode = new exporter().run(
                String.format("--prefix http://en.wikipedia.org --in %s --out %s --threads 1",
                        in.getAbsolutePath(), out.getAbsolutePath())
                        .split(" ")
        );

        Assert.assertEquals(0    , exitCode);
        Assert.assertEquals(11761, FileUtils.readLines(out).size());
    }

}
