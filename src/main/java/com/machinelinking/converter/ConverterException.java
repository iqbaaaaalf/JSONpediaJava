package com.machinelinking.converter;

/**
 * Defines any error raised by a {@link com.machinelinking.converter.Converter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ConverterException extends Exception {

    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
