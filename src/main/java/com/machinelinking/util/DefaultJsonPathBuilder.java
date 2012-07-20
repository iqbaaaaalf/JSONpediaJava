package com.machinelinking.util;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJsonPathBuilder implements JsonPathBuilder {

    private Stack<Element> stack = new Stack<>();

    @Override
    public void startPath() {
        stack.clear();
    }

    @Override
    public void enterArray() {
        stack.push( new ArrayElement() );
    }

    @Override
    public void arrayElem() {
        try {
            final ArrayElement arrayElement = (ArrayElement) stack.peek();
            arrayElement.incIndex();
        } catch(ClassCastException cce) {
            throw new IllegalStateException("Can be invoked within an array");
        }
    }

    @Override
    public void exitArray() {
        try {
            if (!(stack.pop() instanceof ArrayElement)) {
                throw new IllegalStateException("An array must be open first.");
            }
        } catch (EmptyStackException ese) {
            throw new IllegalStateException("Not open array.");
        }
    }

    @Override
    public void enterObject() {
        stack.push( new ObjectElement() );
    }

    @Override
    public void field(String fieldName) {
        try {
            final ObjectElement objectElement = (ObjectElement) stack.peek();
            objectElement.setField(fieldName);
        } catch (ClassCastException cce) {
            throw new IllegalStateException("Can be invoked within an object");
        }
    }

    @Override
    public void exitObject() {
        try {
            if (!(stack.pop() instanceof ObjectElement)) {
                throw new IllegalStateException("An object must be open first.");
            }
        } catch (EmptyStackException ese) {
            throw new IllegalStateException("Not open object.");
        }

    }

    @Override
    public String getJsonPath() {
        if(stack.isEmpty()) throw new IllegalStateException("Outside any element");
        final StringBuilder sb = new StringBuilder();
        sb.append('$');
        for(Element element : stack) {
            sb.append(element.getPathSeparator());
            sb.append(element.toJsonSection()   );
        }
        return sb.toString();
    }

    private interface Element {

        String toJsonSection();
        String getPathSeparator();
    }

    private class ArrayElement implements Element {
        private int index = -1;
        void incIndex() {
            index++;
        }

        @Override
        public String toJsonSection() {
            return String.format("[%s]", index == -1 ? "*" : index);
        }

        @Override
        public String getPathSeparator() {
            return "";
        }

        @Override
        public String toString() {
            return toJsonSection();
        }
    }

    private class ObjectElement implements Element {
        private String field = null;
        void setField(String fieldName) {
            if(fieldName == null || fieldName.trim().length() == 0)
                throw new IllegalArgumentException("Invalid field: " + fieldName);
            field = fieldName;
        }

        @Override
        public String toJsonSection() {
            return field == null ? "" : field;
        }

        @Override
        public String getPathSeparator() {
            return field == null ? "" : ".";
        }

        @Override
        public String toString() {
            return toJsonSection();
        }
    }

}