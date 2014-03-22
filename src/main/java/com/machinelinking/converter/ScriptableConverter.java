package com.machinelinking.converter;

import com.machinelinking.serializer.Serializer;
import com.machinelinking.util.JSONUtils;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Scriptable implementation of {@link com.machinelinking.converter.Converter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
class ScriptableConverter implements Converter {

    public static final String CONVERT_DATA_METHOD = "convert_data";
    public static final String CONVERT_READABLE_METHOD = "convert_hr";

    private final Invocable invocableEngine;

    public ScriptableConverter(Invocable invocableEngine) {
        this.invocableEngine = invocableEngine;
    }

    @Override
    public void convertData(Map<String,?> data, Serializer serializer, Writer writer) throws ConverterException {
        final Object conversion = processMethod(CONVERT_DATA_METHOD, data);
        try {
            JSONUtils.jsonMapArrayToSerializer(conversion, serializer);
        } catch (Exception e) {
            throw new ConverterException("Error while converting conversion data to JSON.", e);
        }
        final String representation = processMethod(CONVERT_READABLE_METHOD, data).toString();
        try {
            writer.append(representation);
        } catch (IOException ioe) {
            throw new IllegalStateException("Error while flusing readable data.", ioe);
        }
    }

    private Object processMethod(String method, Map<String,?> data) throws ConverterException {
        try {
            return invocableEngine.invokeFunction(method, data);
        } catch (ClassCastException cce) {
            throw new ConverterException("Invalid return value from " + method);
        } catch (ScriptException se) {
            throw new ConverterException("Error while evaluating " + method, se);
        } catch (NoSuchMethodException nsme) {
            throw new ConverterException("Script must implement " + method + " method.");
        }
    }

}
