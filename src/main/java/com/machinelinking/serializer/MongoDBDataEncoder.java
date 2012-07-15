package com.machinelinking.serializer;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoDBDataEncoder implements DataEncoder {

    @Override
    public String encodeFieldName(String in) {
        return in.replaceAll("%", "%25").replaceAll("\\$", "%24").replaceAll("\\.", "%2e");
    }

    @Override
    public String encodeFieldValue(String in) {
        return in;
    }

}
