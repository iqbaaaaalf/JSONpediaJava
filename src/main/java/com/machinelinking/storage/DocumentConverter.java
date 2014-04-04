package com.machinelinking.storage;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface DocumentConverter<T extends Document> {

    T convert(T in);

}
