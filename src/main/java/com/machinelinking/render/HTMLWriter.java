package com.machinelinking.render;

import java.io.IOException;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface HTMLWriter {

    void openDocument() throws IOException;

    void closeDocument() throws IOException;

    void openTag(String tag) throws IOException;

    void openTag(String tag, Map<String,String> attributes) throws IOException;

    void closeTag() throws IOException;

    void heading(int level, String title) throws IOException;

    void text(String txt) throws IOException;

    void openColorTag(String color) throws IOException;

    void key(String key) throws IOException;

    void anchor(String url, String text) throws IOException;

    void image(String url, String text) throws IOException;

    void openTable(String title) throws IOException;

    void tableRow(String... cols) throws IOException;

    void closeTable() throws IOException;

    void flush() throws IOException;

}
