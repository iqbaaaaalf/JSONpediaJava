package com.machinelinking.exporter;

import com.machinelinking.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 *  Test case for {@link DefaultCSVExporter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultCSVExporterTest {

    @Test
    public void testExport() throws IOException {
        final DefaultCSVExporter exporter = new DefaultCSVExporter();
        exporter.setThreads(1);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final CSVExporterReport report = exporter.export(
                new URL("http://en.wikipedia.org/"),
                FileUtil.openDecompressedInputStream("/dumps/enwiki-latest-pages-articles-p1.xml.gz"),
                out
        );

        Assert.assertEquals(1133, report.getTemplatesCount());
        final String expected = IOUtils.toString( this.getClass().getResourceAsStream("out.csv") );
        Assert.assertEquals(expected, out.toString());
    }

}
