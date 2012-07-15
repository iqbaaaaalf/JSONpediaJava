package com.machinelinking.pagestruct;

import com.machinelinking.serializer.Serializer;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextSerializerHandlerFactory {

    private static WikiTextSerializerHandlerFactory singleton;

    public static WikiTextSerializerHandlerFactory getInstance() {
        if(singleton == null) singleton = new WikiTextSerializerHandlerFactory();
        return singleton;
    }

    public WikiTextSerializerHandler createSerializerHandler(Serializer serializer) {
        return new WikiTextSerializerHandler(serializer);
    }

    private WikiTextSerializerHandlerFactory() {}

}
