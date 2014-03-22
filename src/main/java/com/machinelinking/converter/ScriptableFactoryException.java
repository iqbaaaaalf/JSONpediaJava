package com.machinelinking.converter;

/**
 * Defines any error raised by {@link com.machinelinking.converter.ScriptableConverterFactory}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ScriptableFactoryException extends Exception {

    public ScriptableFactoryException(String message) {
        super(message);
    }

    public ScriptableFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
