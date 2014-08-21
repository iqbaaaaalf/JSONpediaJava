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

import com.machinelinking.pagestruct.Ontology;
import com.machinelinking.parser.Attribute;
import com.machinelinking.parser.FilteredHandlerCriteria;
import com.machinelinking.parser.WikiTextParserFilteredHandler;
import com.machinelinking.serializer.Serializer;

import java.net.URL;

/**
 * Extractor for page abstracts, does not expand templates.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class AbstractExtractor extends Extractor {

    private final TextHandler textHandler;
    private final WikiTextParserFilteredHandler filteredHandler;

    public AbstractExtractor() {
        super(Ontology.ABSTRACT_FIELD);

        textHandler = new TextHandler();
        filteredHandler = new WikiTextParserFilteredHandler(
                textHandler,
                new FilteredHandlerCriteria() {
                    @Override
                    public boolean mustFilter(int paragraphIndex, int sectionLevel, int nestingLevel) {
                        return paragraphIndex == -1 || sectionLevel != -1;
                    }
                }
        );
    }

    @Override
    public void flushContent(Serializer serializer) {
       serializer.value(textHandler.flushContent());
    }

    @Override
    public void reset() {
        textHandler.reset();
    }

    @Override
    public void beginDocument(URL document) {
        filteredHandler.beginDocument(document);
    }

    @Override
    public void paragraph() {
        filteredHandler.paragraph();
    }

    @Override
    public void section(String title, int level) {
        filteredHandler.section(title, level);
    }

    @Override
    public void beginReference(String label) {
        filteredHandler.beginReference(label);
    }

    @Override
    public void endReference(String label) {
        filteredHandler.endReference(label);
    }

    @Override
    public void beginTag(String name, Attribute[] attributes) {
        filteredHandler.beginTag(name, attributes);
    }

    @Override
    public void endTag(String name) {
        filteredHandler.endTag(name);
    }

    @Override
    public void inlineTag(String name, Attribute[] attributes) {
        filteredHandler.inlineTag(name, attributes);
    }

    @Override
    public void beginLink(URL url) {
        filteredHandler.beginLink(url);
    }

    @Override
    public void endLink(URL url) {
        filteredHandler.endLink(url);
    }

    @Override
    public void beginList() {
        filteredHandler.beginList();
    }

    @Override
    public void listItem(ListType t, int level) {
        filteredHandler.listItem(t, level);
    }

    @Override
    public void endList() {
        filteredHandler.endList();
    }

    @Override
    public void beginTemplate(TemplateName name) {
        filteredHandler.beginTemplate(name);
    }

    @Override
    public void endTemplate(TemplateName name) {
        filteredHandler.endTemplate(name);
    }

    @Override
    public void beginTable() {
        filteredHandler.beginTable();
    }

    @Override
    public void headCell(int row, int col) {
        filteredHandler.headCell(row, col);
    }

    @Override
    public void bodyCell(int row, int col) {
        filteredHandler.bodyCell(row, col);
    }

    @Override
    public void endTable() {
        filteredHandler.endTable();
    }

    @Override
    public void parameter(String param) {
        filteredHandler.parameter(param);
    }

    @Override
    public void text(String content) {
        filteredHandler.text(content);
    }

    @Override
    public void endDocument() {
        filteredHandler.endDocument();
    }

}
