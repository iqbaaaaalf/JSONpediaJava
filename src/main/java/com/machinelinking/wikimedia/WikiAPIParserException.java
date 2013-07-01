package com.machinelinking.wikimedia;

/**
 * Defines an issue while parsing the <i>Wiki API</i> data.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiAPIParserException extends RuntimeException {

    public WikiAPIParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public WikiAPIParserException(String message) {
        super(message);
    }

}
