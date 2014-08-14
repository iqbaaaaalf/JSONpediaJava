package com.machinelinking.util;

import com.machinelinking.filter.DefaultJSONFilterEngine;
import com.machinelinking.filter.JSONFilter;
import com.machinelinking.pagestruct.PageStructConsts;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.codehaus.jackson.util.TokenBuffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <i>JSON</i> utility functions.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONUtils {

    private static final JsonFactory jsonFactory = new JsonFactory();

    private static final ObjectWriter writer;
    private static final ObjectWriter prettyWriter;

    private static final String SPACE_REPLACER = "_";
    private static final Pattern resourcePattern = Pattern.compile("^([a-z\\-]+):([^/]+)$");

    static {
        ObjectMapper mapper = createObjectMapper();
        writer = mapper.writer();
        prettyWriter = writer.withDefaultPrettyPrinter();
    }

    public static URL toResourceURL(String resource) {
        final Matcher resourceMatcher = resourcePattern.matcher(resource);
        final String resourceURL;
        if(resourceMatcher.matches()) {
            final String lang = resourceMatcher.group(1);
            final String document = resourceMatcher
                    .group(2)
                    .replaceAll(" ", SPACE_REPLACER).replaceAll("%20", SPACE_REPLACER);
            resourceURL = String.format("http://%s.wikipedia.org/wiki/%s", lang, document);
        } else {
            resourceURL = resource;
        }
        try {
            return new URL(resourceURL);
        } catch (MalformedURLException murle) {
            throw new IllegalArgumentException(
                    String.format("Invalid resource [%s], must be a valid URL.", resource),
                    murle
            );
        }
    }

    public static JsonNodeFactory getJsonNodeFactory() {
        return JsonNodeFactory.instance;
    }

    public static JsonGenerator createJSONGenerator(Writer writer, boolean format) throws IOException {
        final JsonGenerator generator = jsonFactory.createJsonGenerator(writer);
        if(format) generator.setPrettyPrinter( new DefaultPrettyPrinter() );
        return generator;
    }

    public static JsonGenerator createJSONGenerator(OutputStream os, boolean format) throws IOException {
        return createJSONGenerator( new OutputStreamWriter(os), format );
    }

    public static TokenBuffer createJSONBuffer() {
        return new TokenBuffer(createObjectMapper());
    }

    public static JsonNode bufferToJSONNode(TokenBuffer b) {
        try {
            return createObjectMapper().readTree(b.asParser());
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    public static String bufferToJSONString(TokenBuffer b, boolean format) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final JsonGenerator generator = createJSONGenerator(baos, format);
            generator.setCodec(createObjectMapper());
            generator.writeTree(b.asParser().readValueAsTree());
            return baos.toString();
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    public static TokenBuffer createResultFilteredObject(JsonNode json, JSONFilter filter) throws IOException {
        final JsonNode[] filteredNodes = DefaultJSONFilterEngine.applyFilter(json, filter);
        final TokenBuffer buffer = JSONUtils.createJSONBuffer();
        buffer.writeStartObject();
        buffer.writeObjectField("filter", filter.humanReadable());
        buffer.writeFieldName("result");
        buffer.writeStartArray();
        final ObjectMapper mapper = new ObjectMapper();
        for(JsonNode filteredNode : filteredNodes) {
            mapper.writeTree(buffer, filteredNode);
        }
        buffer.writeEndArray();
        buffer.writeEndObject();
        buffer.close();
        return buffer;
    }

    public static TokenBuffer createResultFilteredObject(TokenBuffer buffer, JSONFilter filter) throws IOException {
        return filter.isEmpty() ?
                buffer
                :
                createResultFilteredObject(
                    JSONUtils.bufferToJSONNode(buffer),
                    filter
                );
    }

    public static ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }

    public static JsonNode parseJSON(InputStream is, boolean checkStreamEmpty) throws IOException {
        JsonParser jsonParser = jsonFactory.createJsonParser(is);
        jsonParser.setCodec( createObjectMapper() );
        final JsonNode node = jsonParser.readValueAsTree();
            if(checkStreamEmpty && is.available() > 0)
                throw new IllegalArgumentException("Invalid JSON stream, should be empty.");
        return node;
    }

    public static JsonNode parseJSON(InputStream is) throws IOException {
        return parseJSON(is, true);
    }

    public static JsonNode parseJSON(byte[] in) throws IOException {
        return parseJSON(new ByteArrayInputStream(in));
    }

    public static JsonNode parseJSON(String in) throws IOException {
        return parseJSON( in.getBytes() );
    }

    public static final JsonNode[] parseJSONMulti(String in) throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(in.getBytes());
        final List<JsonNode> result = new ArrayList<>();
        while(bais.available() > 0) {
            result.add(parseJSON(bais, false));
        }
        return result.toArray(new JsonNode[result.size()]);
    }

    public static Map<String,?> parseJSONAsMap(InputStream is) throws IOException {
        final ObjectMapper mapper = createObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return (Map<String,?>) mapper.readValue(is, Map.class);
    }

    public static Map<String,?> parseJSONAsMap(TokenBuffer b) throws IOException {
        final ObjectMapper mapper = createObjectMapper();
        return (Map<String,?>) mapper.readValue(b.asParser(), Map.class);
    }

    public static Map<String,?> parseJSONAsMap(String in) throws IOException {
        return parseJSONAsMap(new ByteArrayInputStream(in.getBytes()));
    }

    public static void jacksonNodeToSerializer(JsonNode node, Serializer serializer) {
        serializeNode(node, serializer);
    }

    public static void jsonMapArrayToSerializer(Object node, Serializer serializer) {
        serializeNode(node, serializer);
    }

    public static String serializeToJSON(Serializable serializable) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer serializer = new JSONSerializer(baos);
        serializable.serialize(serializer);
        serializer.close();
        return baos.toString();
    }

    public static String serializeToJSON(JsonNode node, boolean format) {
        if(node == null || node.isNull()) return null;
        try {
            final ObjectWriter w = format ? prettyWriter : writer;
            return w.writeValueAsString(node);
        } catch (IOException ioe) {
            throw new IllegalStateException("Unexpected serialization error.", ioe);
        }
    }

    /**
     * Converts a node to primitive string value.
     * 
     * @param node
     * @return
     */
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
        // xxx TODO: INVESTIGATE
        throw new IllegalArgumentException("Unsupported primitive type.");
        //return node.toString();
    }

    /**
     * Converts in a human readable format the content of a JSON object represented as Map/Array object.
     *
     * @param node
     * @return
     */
    public static String toHumanReadable(Object node) {
        final StringBuilder sb = new StringBuilder();
        toHumanReadable(node, sb);
        return sb.toString();
    }

    public static JsonNode toJsonNode(Map<String,?> map) {
        return createObjectMapper().valueToTree(map);
    }

    public static JsonNode getFieldOrFail(String field, JsonNode parent, String errMessage) {
        final JsonNode result = parent.get(field);
        if(result == null) throw new IllegalArgumentException(
                String.format("Cannot find field [%s] in node [%s]: %s", field, parent, errMessage)
        );
        return result;
    }

    // BEGIN: convert JsonNode to Serializer events.

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
        } else if(node.isNull()){
            serializer.value(null);
        } else {
            throw new IllegalArgumentException();
        }
    }

    // END:   convert JsonNode to Serializer events.

    // BEGIN: convert Map<String,?> to Serializer events.

    private static void serializeArray(Object[] array, Serializer serializer) {
           serializer.openList();
           for(int i = 0; i < array.length; i++) {
               serializeNode(array[i], serializer);
           }
           serializer.closeList();
    }

    private static void serializeObject(Map<String,?> map, Serializer serializer) {
        serializer.openObject();
        for (Map.Entry<String,?> entry : map.entrySet()) {
            serializer.field(entry.getKey());
            serializeNode(entry.getValue(), serializer);
        }
        serializer.closeObject();
    }

    private static void serializeNode(Object node, Serializer serializer) {
        if(node.getClass().isArray()) {
            serializeArray((Object[]) node, serializer);
        } else if(node instanceof Map) {
            serializeObject((Map<String, ?>) node, serializer);
        } else if(node instanceof String) {
            serializer.value( node.toString() );
        } else if(isPrimitive(node.getClass())){
            serializer.value(node);
        } else {
            throw new IllegalArgumentException("Invalid node: " + node.toString());
        }
    }

    // END:   convert Map<String,?> to Serializer events.

    // BEGIN: Map<String,?> toHumanReadable

    protected static void toHumanReadable(Object[] a, StringBuilder sb) {
        for (Object e : a) {
            sb.append(" ");
            toHumanReadable(e, sb);
        }
    }

    private static void toHumanReadable(Map<String,?> m, StringBuilder sb) {
        for (Map.Entry<String,?> e : m.entrySet()) {
            if(PageStructConsts.TYPE_FIELD.equals(e.getKey())) continue;
            if(PageStructConsts.CONTENT_FIELD.equals(e.getKey())) {
                toHumanReadable(e.getValue(), sb);
                continue;
            }
            if(e.getKey().startsWith(PageStructConsts.ANON_NAME_PREFIX)) {
                toHumanReadable(e.getValue(), sb);
                continue;
            }
            sb.append(e.getKey());
            toHumanReadable(e.getValue(), sb);
        }
    }

    private static void toHumanReadable(Object node, StringBuilder sb) {
        if (node.getClass().isArray()) {
            toHumanReadable((Object[]) node, sb);
        } else if (node instanceof Map) {
            toHumanReadable((Map<String, ?>) node, sb);
        } else if (node instanceof String || isPrimitive(node.getClass())) {
            sb.append(node.toString());
        } else {
            throw new IllegalArgumentException("Invalid node: " + node.toString());
        }
    }

    // END  : Map<String,?> toHumanReadable

    public static boolean isPrimitive(Class c) {
        if (c.isPrimitive()) return true;
        if (
                c == Byte.class
                    || c == Short.class
                    || c == Integer.class
                    || c == Long.class
                    || c == Float.class
                    || c == Double.class
                    || c == Boolean.class
                    || c == Character.class
        ) return true;
        return false;
    }

    public static Iterable<Map.Entry<String,JsonNode>> toIterable(final Iterator<Map.Entry<String,JsonNode>> iter) {
        return new Iterable<Map.Entry<String,JsonNode>>() {
            @Override
            public Iterator<Map.Entry<String,JsonNode>> iterator() {
                return iter;
            }
        };
    }

    private JSONUtils() {}

}
