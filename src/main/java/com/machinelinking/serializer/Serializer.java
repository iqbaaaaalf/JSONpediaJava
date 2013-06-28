package com.machinelinking.serializer;

/**
 * Event based serializer interface.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Serializer {

    void setDataEncoder(DataEncoder encoder);

    DataEncoder getDataEncoder();

    void openObject();

    void closeObject();

    void openList();

    void closeList();

    void field(String name);

    void value(Object value);

    void fieldValue(String name, Object value);

    void fieldValueIfNotNull(String name, Object value);

    void flush();

    void close();

}
