package com.machinelinking.serializer;

/**
 * Allows to serialize any object with the {@link Serializer}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Serializable {

    void serialize(Serializer serializer);

}
