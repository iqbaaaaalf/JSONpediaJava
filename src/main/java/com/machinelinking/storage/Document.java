package com.machinelinking.storage;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Document<T> {

    String getId();

    T getDocument();

}
