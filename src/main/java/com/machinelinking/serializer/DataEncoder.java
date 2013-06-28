package com.machinelinking.serializer;

/**
 * Contains logic for encoding field names and values.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface DataEncoder {

    String encodeFieldName(String in);

    String encodeFieldValue(String in);

}
