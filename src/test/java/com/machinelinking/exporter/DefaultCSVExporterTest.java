package com.machinelinking.exporter;

import com.machinelinking.util.FileUtil;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultCSVExporterTest {

    @Test
    public void testExport() throws IOException {
        final CSVExporter exporter = new DefaultCSVExporter();
        exporter.export(
                new URL("http://en.wikipedia.org/"),
                FileUtil.openResource("file:///Users/hardest/Downloads/enwiki-latest-pages-articles7.xml-p000305002p000464996.bz2"),
                new FileOutputStream("/Users/hardest/Desktop/appo.csv")
        );
    }

}
