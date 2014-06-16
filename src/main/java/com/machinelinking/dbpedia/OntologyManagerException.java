package com.machinelinking.dbpedia;

/**
 * Any exception rasised by the {@link OntologyManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class OntologyManagerException extends Exception {

    public OntologyManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public OntologyManagerException(Throwable t) {
        super(t);
    }

}
