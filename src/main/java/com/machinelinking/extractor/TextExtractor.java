package com.machinelinking.extractor;

import com.machinelinking.pagestruct.WikiTextSerializerHandler;
import com.machinelinking.pagestruct.WikiTextSerializerHandlerFactory;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.Serializer;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

/**
 * Extracts the text content from the page, replaces any structured content with a special syntax containing JSON.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TextExtractor extends Extractor {

    private final StringWriter writer;
    private final WikiTextSerializerHandler decoratedHandler;

    private final StringBuilder outputBuffer = new StringBuilder();

    private int nestedStructures = 0;

    protected TextExtractor() {
        super("text");

        writer = new StringWriter();
        final Serializer serializer;
        try {
            serializer = new JSONSerializer(writer);
        } catch (IOException ioe) {
            throw new IllegalStateException();
        }
        decoratedHandler = WikiTextSerializerHandlerFactory.getInstance().createSerializerHandler(serializer);
        decoratedHandler.reset();
    }

    @Override
    public void flushContent(Serializer serializer) {
        serializer.value(outputBuffer.toString());
    }

    @Override
    public void reset() {
        nestedStructures = 0;
        decoratedHandler.reset();
        outputBuffer.delete(0, outputBuffer.length());
    }

    @Override
    public void section(String title, int level) {
        decoratedHandler.section(title, level);
    }

    @Override
    public void beginReference(String label) {
        nestedStructures++;
        decoratedHandler.beginReference(label);
    }

    @Override
    public void endReference(String label) {
        decoratedHandler.endReference(label);
        nestedStructures--;
    }

    @Override
    public void beginLink(URL url) {
        nestedStructures++;
        decoratedHandler.beginLink(url);
    }

    @Override
    public void endLink(URL url) {
        decoratedHandler.endLink(url);
        nestedStructures--;
    }

    @Override
    public void beginList() {
        nestedStructures++;
        decoratedHandler.beginList();
    }

    @Override
    public void listItem(ListType t, int level) {
        decoratedHandler.listItem(t, level);
    }

    @Override
    public void endList() {
        decoratedHandler.endList();
        nestedStructures--;
    }

    @Override
    public void beginTemplate(String name) {
        nestedStructures++;
        decoratedHandler.beginTemplate(name);
    }

    @Override
    public void endTemplate(String name) {
        decoratedHandler.endTemplate(name);
        nestedStructures--;
    }

    @Override
    public void text(String content) {
        if(nestedStructures < 0) throw new AssertionError("Invalid value, must be >= 0");
        if(nestedStructures > 0) {
            decoratedHandler.text(content);
        } else {
            decoratedHandler.flush();
            decoratedHandler.reset();
            final StringBuffer buffer = writer.getBuffer();
            if(buffer.length() > 0) {
                outputBuffer.append("<%");
                outputBuffer.append(buffer);
                buffer.delete(0, buffer.length());
                outputBuffer.append("%>");
            }
            outputBuffer.append(content);
        }
    }

}
