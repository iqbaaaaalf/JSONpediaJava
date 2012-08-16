package com.machinelinking.exporter;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface CSVExporter {

    CSVExporterReport export(URL pagePrefix, InputStream is, OutputStream os);

}
