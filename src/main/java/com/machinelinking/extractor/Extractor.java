package com.machinelinking.extractor;

import com.machinelinking.parser.DefaultWikiTextParserHandler;
import com.machinelinking.serializer.Serializer;

/**
 * Defines an extractor for a <i>Wikipedia</i> specific feature.
 *
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
