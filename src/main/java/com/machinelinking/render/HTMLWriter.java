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

package com.machinelinking.render;

import java.io.IOException;
import java.util.Map;

/**
 * Auxiliary class to write <i>HTML</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface HTMLWriter {

    void openDocument(String title) throws IOException;

    void closeDocument() throws IOException;

    void openTag(String tag) throws IOException;

    void openTag(String tag, Map<String,String> attributes) throws IOException;

    void closeTag() throws IOException;

    void heading(int level, String title) throws IOException;

    void html(String content) throws IOException;

    void text(String txt) throws IOException;

    void openColorTag(String color) throws IOException;

    void key(String key) throws IOException;

    void anchor(String url, String text, boolean internal) throws IOException;

    void anchor(String name) throws IOException;

    void image(String url, String text) throws IOException;

    void openList() throws IOException;

    void openListItem() throws IOException;

    void closeListItem() throws IOException;

    void closeList() throws IOException;

    void openTable(String title, Map<String,String> attributes) throws IOException;

    void openTableRow() throws IOException;

    void closeTableRow() throws IOException;

    void openTableCol() throws IOException;

    void closeTableCol() throws IOException;

    void tableRow(String... cols) throws IOException;

    void closeTable() throws IOException;

    void br() throws IOException;

    void em() throws IOException;

    void link(String description, String url) throws IOException;

    void reference(String description, String lang, String label, boolean internal) throws IOException;

    void templateReference(String description, String lang, String label) throws IOException;

    void category(String lang, String category) throws IOException;

    void span(String content) throws IOException;

    void flush() throws IOException;
}
