package com.machinelinking.util;

import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONUtils {

    private static final JsonFactory jsonFactory = new JsonFactory();

    public static JsonGenerator createJSONGenerator(Writer writer, boolean format) throws IOException {
        final JsonGenerator generator = jsonFactory.createJsonGenerator(writer);
        if(format)
            generator.setPrettyPrinter( new DefaultPrettyPrinter() );
        return generator;
    }

    public static JsonGenerator createJSONGenerator(OutputStream os, boolean format) throws IOException {
        return createJSONGenerator( new OutputStreamWriter(os), format );
    }

    public static ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }

    public static JsonNode parseJSON(InputStream is) throws IOException {
        JsonParser jsonParser = jsonFactory.createJsonParser(is);
        jsonParser.setCodec( createObjectMapper() );
        final JsonNode node = jsonParser.readValueAsTree();
        if(is.available() > 0) throw new IllegalArgumentException("Invalid JSON closure.");
        return node;
    }

    public static JsonNode parseJSON(byte[] in) throws IOException {
        return parseJSON(new ByteArrayInputStream(in));
    }

    public static JsonNode parseJSON(String in) throws IOException {
        return parseJSON( in.getBytes() );
    }

    public static void jacksonNodeToSerializer(JsonNode node, Serializer serializer) {
        serializeNode(node, serializer);
    }

    public static String serializeToJSON(Serializable serializable) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer serializer = new JSONSerializer(baos);
        serializable.serialize(serializer);
        serializer.close();
        return baos.toString();
    }

    public static String asPrimitiveString(JsonNode node) {
        if(node == null || node.isNull()) return null;
        if(node.isTextual() || node.isNumber()) {
            return node.asText();
        }
        if(node.isArray()) {
            final int size = node.size();
            if(size == 1) {
                return node.get(0).asText();
            } else if(size == 0) {
                return null;
            } else {
                throw new IllegalArgumentException("Invalid array size");
            }
        }
        throw new IllegalArgumentException("Unsupported primitive type.");
    }

    private static void serializeArray(JsonNode node, Serializer serializer) {
        serializer.openList();
        for(int i = 0; i < node.size(); i++) {
            serializeNode(node.get(i), serializer);
        }
        serializer.closeList();
    }

    private static void serializeObject(JsonNode node, Serializer serializer) {
        Iterator<Map.Entry<String, JsonNode>> entries = node.getFields();
        Map.Entry<String, JsonNode> entry;
        serializer.openObject();
        while (entries.hasNext()) {
            entry = entries.next();
            serializer.field(entry.getKey());
            serializeNode(entry.getValue(), serializer);
        }
        serializer.closeObject();
    }

    private static void serializeNode(JsonNode node, Serializer serializer) {
        if(node.isArray()) {
            serializeArray(node, serializer);
        } else if(node.isObject()) {
            serializeObject(node, serializer);
        } else if(node.isTextual()) {
            serializer.value( node.asText() );
        } else if(node.isDouble()) {
            serializer.value( node.asDouble() );
        } else if(node.isInt()) {
            serializer.value( node.asInt() );
        } else if(node.isBoolean()) {
            serializer.value( node.asBoolean() );
        } else {
            throw new IllegalArgumentException();
        }
    }

    private JSONUtils() {}

}
