package com.machinelinking.template;

import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class  DefaultTemplateCall implements TemplateCall {

    private final JsonNode name;
    private final Parameter[] parameters;
    private final Map<String,Parameter> parametersMap;

    public DefaultTemplateCall(JsonNode name, Parameter[] params) {
        this.name = name;
        this.parameters = params;
        this.parametersMap = new HashMap<>();
        for(Parameter param : params) {
            parametersMap.put(param.name, param);
        }
    }

    @Override
    public JsonNode getName() {
        return name;
    }

    @Override
    public Parameter[] getParameters() {
        return parameters;
    }

    @Override
    public String[] getParameterNames() {
        return parametersMap.keySet().toArray( new String[0] );
    }

    @Override
    public JsonNode getParameter(String param) {
        final Parameter match = parametersMap.get(param);
        return match == null ? null : match.value;
    }

    @Override
    public JsonNode getParameter(int index) {
        if(index >= parameters.length) return null;
        return parameters[index].value;
    }

    @Override
    public int getParametersCount() {
        return parametersMap.size();
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", this.name, this.parametersMap.toString());
    }

}
