package com.machinelinking.parser;

import java.io.IOException;

/**
 * Defines the reader for a {@link WikiTextParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface ParserReader {

    char read() throws IOException;

    void mark() throws IOException;

    void reset() throws IOException;

    ParserLocation getLocation();

}
