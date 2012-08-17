package com.machinelinking.exporter;

import com.machinelinking.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
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
                FileUtil.openResource("/enwiki-latest-pages-articles-p1.xml.gz"),
                out
        );

        System.out.println("REPORT: " + report);
        System.out.println("OUT:\n"   + out);
        final String expected = IOUtils.toString( this.getClass().getResourceAsStream("/exporter/out.csv") );
        Assert.assertEquals(expected, out.toString());
    }

}
