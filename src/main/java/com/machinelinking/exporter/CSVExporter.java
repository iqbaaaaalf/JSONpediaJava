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
