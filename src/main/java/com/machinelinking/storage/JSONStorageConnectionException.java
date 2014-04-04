package com.machinelinking.storage;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONStorageConnectionException extends Exception {

    public JSONStorageConnectionException(String message) {
        super(message);
    }

    public JSONStorageConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

}
