package com.machinelinking.parser;

import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultWikiTextParserHandler implements WikiTextParserHandler {

    @Override
    public void beginDocument(URL document) {
    }

    @Override
    public void parseWarning(String msg, int row, int col) {
    }

    @Override
    public void parseError(Exception e, int row, int col) {
    }

    @Override
    public void section(String title, int level) {
    }

    @Override
    public void reference(String label, String description) {
    }

    @Override
    public void link(String url, String description) {
    }

    @Override
    public void beginList() {
    }

    @Override
    public void listItem() {
    }

    @Override
    public void endList() {
    }

    @Override
    public void beginTemplate(String name) {
    }

    @Override
    public void templateParameterName(String param) {
    }

    @Override
    public void endTemplate(String name) {
    }

    @Override
    public void beginTable() {
    }

    @Override
    public void headCell(int row, int col) {
    }

    @Override
    public void bodyCell(int row, int col) {
    }

    @Override
    public void endTable() {
    }

    @Override
    public void text(String content) {
    }

    @Override
    public void endDocument() {
    }

}
