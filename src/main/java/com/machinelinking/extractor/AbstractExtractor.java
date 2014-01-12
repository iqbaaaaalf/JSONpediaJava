package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializer;

import java.net.URL;

/**
 * Extractor for page abstracts, does not expand templates.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class AbstractExtractor extends Extractor {

    private final StringBuilder textBuffer = new StringBuilder();

    private int nesting;
    private boolean completed;

    public AbstractExtractor() {
        super("abstract");
    }

    @Override
    public void beginDocument(URL document) {
        nesting = 0;
        completed = false;
    }

    @Override
    public void beginTemplate(String name) {
        nesting++;
    }

    @Override
    public void beginTable() {
        nesting++;
    }

    @Override
    public void endTemplate(String name) {
        nesting--;
    }

    @Override
    public void endTable() {
        nesting--;
    }

    @Override
    public void var(Var v) {
        // Empty.
    }

    @Override
    public void beginReference(String label) {
        // Empty.
    }

    @Override
    public void text(String content) {
        processText(content);
    }

    @Override
    public void flushContent(Serializer serializer) {
        serializer.value(textBuffer.toString());
    }

    @Override
    public void reset() {
        textBuffer.delete(0, textBuffer.length());
    }

    private void processText(String content) {
        if (completed) return;
        if (nesting > 0) return;
        String toAppend = content;
        if (content.trim().length() > 2) {
            int endIndex = content.indexOf("\n\n");
            if(endIndex != -1) {
                completed = true;
                toAppend = content.substring(0, endIndex);
            }
        }
        textBuffer.append(toAppend);
    }

}
