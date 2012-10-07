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
    public void parseWarning(String msg, ParserLocation location) {
    }

    @Override
    public void parseError(Exception e, ParserLocation location) {
    }

    @Override
    public void section(String title, int level) {
    }

    @Override
    public void beginReference(String label) {
    }

    @Override
    public void endReference(String label) {
    }

    @Override
    public void beginLink(URL url) {
    }

    @Override
    public void endLink(URL url) {
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
    public void beginTag(String name, Attribute[] attributes) {
    }

    @Override
    public void endTag(String name) {
    }

    @Override
    public void inlineTag(String name, Attribute[] attributes) {
    }

    @Override
    public void commentTag(String comment) {
    }

    @Override
    public void parameter(String param) {
    }

    @Override
    public void text(String content) {
    }

    @Override
    public void endDocument() {
    }

}
