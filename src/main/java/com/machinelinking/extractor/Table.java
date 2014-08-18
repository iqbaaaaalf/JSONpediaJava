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
