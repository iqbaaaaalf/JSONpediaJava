package com.machinelinking.dbpedia;

/**
 * Exception raised by {@link com.machinelinking.dbpedia.TemplateMappingManager}.
 *
 * @see com.machinelinking.dbpedia.TemplateMappingManager
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateMappingManagerException extends Exception {

    public TemplateMappingManagerException(String message) {
        super(message);
    }

    public TemplateMappingManagerException(String message, Throwable cause) {
        super(message, cause);
    }

}
