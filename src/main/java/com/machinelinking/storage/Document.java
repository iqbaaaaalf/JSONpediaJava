package com.machinelinking.storage;

/**
 * Defines a <i>Wikitext</i> document.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Document<T> {

    int getId();

    int getVersion();

    String getName();

    T getContent();

    String toJSON();

}
