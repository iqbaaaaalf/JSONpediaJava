package com.machinelinking.serializer;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Stack;

/**
 * Serializes events to <i>JSON</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
//TODO: remove print out but before check for possible errors.
public class JSONSerializer implements Serializer {

    private static final String ANON_FIELD_PREFIX = "__an";

    private enum WriterStatus {
        Object {
            @Override
            void close(JsonGenerator generator) {
                try {
                    generator.writeEndObject();
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }

            }
        },
        PreList {
            @Override
            void close(JsonGenerator generator) {
                try {
                    generator.writeNull();
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        },
        List {
            @Override
            void close(JsonGenerator generator) {
                try {
                    generator.writeEndArray();
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        },
        Field {
            @Override
            void close(JsonGenerator generator) {
                try {
                    generator.writeNull();
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        },
        SpuriousField {
            @Override
            void close(JsonGenerator generator) {
                try {
                    generator.writeNull();
                    generator.writeEndObject();
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }

            }
        };

        abstract void close(JsonGenerator generator);
    }

    private final JsonGenerator jsonGenerator;

    private final Stack<WriterStatus> stack = new Stack<WriterStatus>();
    private final Stack<Integer> indexStack = new Stack<>();

    private DataEncoder dataEncoder;

    public JSONSerializer(JsonGenerator jsonGenerator) throws IOException {
        if(jsonGenerator == null) throw new NullPointerException("JSON generator cannot be null.");
        this.jsonGenerator = jsonGenerator;
    }

    public JSONSerializer(Writer writer) throws IOException {
        this( JSONUtils.createJSONGenerator(writer, false) );
    }

    public JSONSerializer(OutputStream os) throws IOException {
        this( new OutputStreamWriter(os) );
    }

    @Override
    public void setDataEncoder(DataEncoder encoder) {
        dataEncoder = encoder;
    }

    @Override
    public DataEncoder getDataEncoder() {
        return dataEncoder;
    }

    @Override
    public void openObject() {
        try {
            if( check(WriterStatus.Object) ) {
                jsonGenerator.writeFieldName(getNextAnonField());
                jsonGenerator.writeStartObject();
            } else if( checkAndPop(WriterStatus.PreList) ) {
                stackPush(WriterStatus.List);
                jsonGenerator.writeStartArray();
                jsonGenerator.writeStartObject();
            } else if( check(WriterStatus.List) ) {
                jsonGenerator.writeStartObject();
            } else if( checkAndPop(WriterStatus.Field) ) {
                jsonGenerator.writeStartObject();
            } else if( checkAndPop(WriterStatus.SpuriousField) ) {
                jsonGenerator.writeNull();
                jsonGenerator.writeEndObject();
                jsonGenerator.writeStartObject();
            } else {
                jsonGenerator.writeStartObject();
            }
            stackPush(WriterStatus.Object);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void closeObject() {
        try {
            if( checkAndPop(WriterStatus.Object) ) {
                jsonGenerator.writeEndObject();
            } else {
                closeUntil(WriterStatus.Object);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void openList() {
        try {
            if( check(WriterStatus.Object) ) {
                jsonGenerator.writeFieldName(getNextAnonField());
            } else if( check(WriterStatus.List) ) {
            } else if( checkAndPop(WriterStatus.PreList) ) {
                stackPush(WriterStatus.List);
                jsonGenerator.writeStartArray();
            } else if( checkAndPop(WriterStatus.Field) ) {
            } else if( checkAndPop(WriterStatus.SpuriousField) ) {
                jsonGenerator.writeNull();
                jsonGenerator.writeEndObject();
            }
            stackPush(WriterStatus.PreList);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void closeList() {
        try {
            if( checkAndPop(WriterStatus.List) ) {
                jsonGenerator.writeEndArray();
            } else if( checkAndPop(WriterStatus.PreList) ) {
                jsonGenerator.writeNull();
            } else {
                closeUntil(WriterStatus.List);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void field(String name) {
        final String finalName = name == null ? getNextAnonField() : encodeFieldName(name);
        try {
            if( check(WriterStatus.Object) ) {
                jsonGenerator.writeFieldName(finalName);
                stackPush(WriterStatus.Field);
            } else if(checkAndPop(WriterStatus.PreList)) {
                stackPush(WriterStatus.List);
                System.out.println("SPURIOUS A");
                jsonGenerator.writeStartArray();
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName(finalName);
                stackPush(WriterStatus.SpuriousField);
            } else if(check(WriterStatus.List)) {
                System.out.println("SPURIOUS B");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName(finalName);
                stackPush(WriterStatus.SpuriousField);
            } else if( checkAndPop(WriterStatus.SpuriousField) ) {
                jsonGenerator.writeNull();
                jsonGenerator.writeEndObject();
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName(finalName);
                stackPush(WriterStatus.SpuriousField);
            }else { // Field
                System.out.println("NESTED");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName(finalName);
                stackPush(WriterStatus.SpuriousField);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void value(Object value) {
        try {
            if ( check(WriterStatus.Object) ) {
                jsonGenerator.writeObjectField(getNextAnonField(), value);
            } else if( checkAndPop(WriterStatus.PreList) ) {
                stackPush(WriterStatus.List);
                jsonGenerator.writeStartArray();
                internalWriteValue(value);
            } else if( check(WriterStatus.List) ) {
                internalWriteValue(value);
            } else if( checkAndPop(WriterStatus.Field) ) {
                internalWriteValue(value);
            } else if( checkAndPop(WriterStatus.SpuriousField) ) {
                internalWriteValue(value);
                jsonGenerator.writeEndObject();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void fieldValue(String name, Object value) {
        this.field(name);
        this.value(value);
    }

    @Override
    public void fieldValueIfNotNull(String name, Object value) {
        if(value != null) fieldValue(name, value);
    }

    @Override
    public void flush() {
        try {
            jsonGenerator.flush();
        } catch (IOException ioe) {
            throw new RuntimeException("Error while flushing JSON generator.", ioe);
        }
    }

    private void internalWriteValue(Object v) {
        try {
            if (v instanceof String) {
                final String vString = (String) v;
                jsonGenerator.writeString(encodeFieldValue(vString));
            } else if(v == null) {
                jsonGenerator.writeNull();
            } else {
                final Class vClass = v.getClass();
                if(Integer.class.equals(vClass)        || int.class.equals(vClass)) {
                    jsonGenerator.writeNumber((Integer) v);
                } else if(Long.class.equals(vClass)    || long.class.equals(vClass)) {
                    jsonGenerator.writeNumber((Long) v);
                } else if(Float.class.equals(vClass)   || float.class.equals(vClass)) {
                    jsonGenerator.writeNumber((Float) v);
                } else if(Double.class.equals(vClass)  || double.class.equals(vClass)) {
                    jsonGenerator.writeNumber((Double) v);
                } else if(Boolean.class.equals(vClass) || boolean.class.equals(vClass)) {
                    jsonGenerator.writeBoolean((Boolean) v);
                } else {
                    throw new IllegalArgumentException("Unsupported value class " + vClass);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while writing value " + v, e);
        }
    }

    private void stackPush(WriterStatus status) {
        stack.push(status);
        indexStack.push(0);
    }

    private boolean checkAndPop(WriterStatus status) {
        if(stack.isEmpty()) return false;
        if(stack.peek() == status) {
            stack.pop();
            indexStack.pop();
            return true;
        } else {
            return false;
        }
    }

    private boolean check(WriterStatus status) {
        return ! stack.isEmpty() && stack.peek() == status;
    }

    private void closeUntil(WriterStatus target) {
        if( target == null) {
            System.out.println("CLOSE ALL {");
        } else {
            System.out.println("CLOSE UNTIL " + target + "{");
        }
        WriterStatus status;
        while(! stack.isEmpty()) {
            status = stack.pop();
            status.close(jsonGenerator);
            System.out.println("CLOSED: " + status);
            if(target != null && status == target) break;
        }
        System.out.println("}");
    }

    public void close() {
        indexStack.clear();
        closeUntil(null);
        try {
            jsonGenerator.flush();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private String encodeFieldName(String field) {
        if(dataEncoder == null) return field;
        return dataEncoder.encodeFieldName(field);
    }

    private String encodeFieldValue(String value) {
        if(dataEncoder == null) return value;
        return dataEncoder.encodeFieldValue(value);
    }

    private String getNextAnonField() {
        final int value = indexStack.pop();
        indexStack.push(value + 1);
        return ANON_FIELD_PREFIX + value;
    }

}
