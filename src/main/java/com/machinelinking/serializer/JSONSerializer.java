package com.machinelinking.serializer;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Stack;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONSerializer implements Serializer {

    private static final String ANON_OBJ_FIELD_PREFIX  = "anon_obj_";
    private static final String ANON_LIST_FIELD_PREFIX = "anon_lst_";

    private long anonObjId   = 0;
    private long anonFieldId = 0;

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
                jsonGenerator.writeFieldName(getNextAnonObjectName());
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
            stack.push(WriterStatus.Object);
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
                jsonGenerator.writeFieldName(getNextAnonListName());
            } else if( check(WriterStatus.List) ) {
            } else if( checkAndPop(WriterStatus.Field) ) {
            } else if( checkAndPop(WriterStatus.SpuriousField) ) {
                jsonGenerator.writeNull();
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeStartArray();
            stack.push(WriterStatus.List);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void closeList() {
        try {
            if( checkAndPop(WriterStatus.List) ) {
                jsonGenerator.writeEndArray();
            } else {
                closeUntil(WriterStatus.List);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void field(String name) {
        try {
            if( check(WriterStatus.Object) ) {
                jsonGenerator.writeFieldName( encodeFieldName(name) );
                stack.push(WriterStatus.Field);
            } else if(check(WriterStatus.List)) {
                System.out.println("SPURIOUS");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName( encodeFieldName(name) );
                stack.push(WriterStatus.SpuriousField);
            } else if( checkAndPop(WriterStatus.SpuriousField) ) {
                jsonGenerator.writeNull();
                jsonGenerator.writeEndObject();
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName( encodeFieldName(name) );
                stack.push(WriterStatus.SpuriousField);
            }else { // Field
                System.out.println("NESTED");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName( encodeFieldName(name) );
                stack.push(WriterStatus.SpuriousField);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void value(Object value) {
        try {
            if ( check(WriterStatus.Object) ) {
                System.out.println("NO FIELD");
                jsonGenerator.writeObjectField("NO FIELD", value);
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

    private boolean checkAndPop(WriterStatus status) {
        if(stack.isEmpty()) return false;
        if(stack.peek() == status) {
            stack.pop();
            return true;
        } else {
            System.out.printf("ERROR, expected %s found %s\n", status, stack.peek());
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
        anonFieldId = anonObjId = 0;
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

    private String getNextAnonObjectName() {
        return ANON_OBJ_FIELD_PREFIX + anonObjId++;
    }

    private String getNextAnonListName() {
        return ANON_LIST_FIELD_PREFIX + anonFieldId++;
    }

}
