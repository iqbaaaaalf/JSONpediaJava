package com.machinelinking.main;

/**
 * Describes any error raised by the {@link com.machinelinking.main.JSONpedia} facade.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONpediaException extends Exception {

    public JSONpediaException(String message) {
        super(message);
    }

    public JSONpediaException(String message, Throwable cause) {
        super(message, cause);
    }

}
