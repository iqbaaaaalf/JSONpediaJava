package com.machinelinking.util;

import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONUtils {

    private static final JsonFactory jsonFactory = new JsonFactory();

    public static JsonGenerator createJSONGenerator(Writer writer) throws IOException {
        final JsonGenerator generator = jsonFactory.createJsonGenerator(writer);
        //generator.setPrettyPrinter( new DefaultPrettyPrinter() );
        return generator;
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
        } else {
            serializer.value( node.getTextValue() ); // TODO: manage objects.
        }
    }

    private JSONUtils() {}

}
