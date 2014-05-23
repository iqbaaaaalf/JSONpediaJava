package com.machinelinking.template;

import org.codehaus.jackson.JsonNode;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TemplateCall {

    JsonNode getName();

    Parameter[] getParameters();

    String[] getParameterNames();

    JsonNode getParameter(String param);

    JsonNode getParameter(int index);

    int getParametersCount();

    public class Parameter {
        public final String name;
        public final JsonNode value;
        public Parameter(String name, JsonNode value) {
            this.name = name;
            this.value = value;
        }
        @Override
        public String toString() {
            return String.format("%s:%s", name, value);
        }
    }

}
