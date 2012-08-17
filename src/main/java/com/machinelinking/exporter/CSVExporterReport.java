package com.machinelinking.exporter;

import com.machinelinking.wikimedia.ProcessorReport;

/**
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
                "processor: %s - templates: %d, properties %d, max properties/template: %d, avg properties/template: %f\n",
                processorReport, templatesCount, propertiesCount, maxPropertiesPerTemplate, (float)propertiesCount / templatesCount
        );
    }

}
