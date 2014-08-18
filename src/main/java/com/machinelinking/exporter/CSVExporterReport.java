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

import com.machinelinking.wikimedia.ProcessorReport;

/**
 * Execution report for {@link CSVExporter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CSVExporterReport {

    private final ProcessorReport processorReport;
    private final long templatesCount;
    private final long propertiesCount;
    private final int  maxPropertiesPerTemplate;


    public CSVExporterReport(
            ProcessorReport processorReport,
            long templatesCount, long propertiesCount, int maxPropertiesPerTemplate
    ) {
        this.processorReport = processorReport;
        this.templatesCount = templatesCount;
        this.propertiesCount = propertiesCount;
        this.maxPropertiesPerTemplate = maxPropertiesPerTemplate;
    }

    public ProcessorReport getProcessorReport() {
        return processorReport;
    }

    public long getTemplatesCount() {
        return templatesCount;
    }

    public long getPropertiesCount() {
        return propertiesCount;
    }

    public int getMaxPropertiesPerTemplate() {
        return maxPropertiesPerTemplate;
    }

    @Override
    public String toString() {
        return String.format(
            "processor: %s\n\ttemplates: %d, properties %d, max properties/template: %d, avg properties/template: %f\n",
            processorReport, templatesCount, propertiesCount, maxPropertiesPerTemplate,
                (float)propertiesCount / templatesCount
        );
    }

}
