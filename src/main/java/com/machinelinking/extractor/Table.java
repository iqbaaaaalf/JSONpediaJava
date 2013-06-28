package com.machinelinking.extractor;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a <i>Wikipedia table</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Table {

    private String identifier;
    private List<String> headers       = new ArrayList<String>();
    private List<List<String>> content = new ArrayList<List<String>>();

    public Table(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<List<String>> getContent() {
        return content;
    }

    protected void addHeader(String header) {
        headers.add(header);
    }

    protected void addRow(List<String> row) {
        content.add(row);
    }
}
