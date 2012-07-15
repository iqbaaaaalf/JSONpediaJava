package com.machinelinking.serializer;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface DataEncoder {

    String encodeFieldName(String in);

    String encodeFieldValue(String in);

}
