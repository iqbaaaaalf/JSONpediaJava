package com.machinelinking.exporter;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * <i>CSV</i> data exporter.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface CSVExporter {

    /**
     * Exports a given Wiki page fetched from the specified input stream <code>is</code>
     * to the specified output stream <code>os</code> as <i>CSV</i>.
     *
     * @param pagePrefix
     * @param is
     * @param os
     * @return the report related to the exporting activity.
     */
    CSVExporterReport export(URL pagePrefix, InputStream is, OutputStream os);

}
