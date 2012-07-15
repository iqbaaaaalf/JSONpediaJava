package com.machinelinking.extractor;

import com.machinelinking.parser.DefaultWikiTextParserHandler;
import com.machinelinking.serializer.Serializer;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class Extractor extends DefaultWikiTextParserHandler {

    private final String name;

    protected Extractor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void flushContent(Serializer serializer);

    public abstract void reset();

}
