package com.machinelinking.extractor;

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
        super("abstract");

        textHandler = new TextHandler();
        filteredHandler = new WikiTextParserFilteredHandler(
                textHandler,
                new FilteredHandlerCriteria() {
                    @Override
                    public boolean mustFilter(int paragraphIndex, int sectionLevel, int nestingLevel) {
                        return paragraphIndex == 0 || sectionLevel != -1;
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
    public void text(String content) {
        filteredHandler.text(content);
    }

    @Override
    public void endDocument() {
        filteredHandler.endDocument();
    }

}
