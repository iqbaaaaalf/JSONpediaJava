package com.machinelinking.parser;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface ParserReader {

    char read() throws IOException;

    void mark() throws IOException;

    void reset() throws IOException;

    ParserLocation getLocation();

}
