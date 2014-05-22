package com.machinelinking.template;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class  DefaultTemplateCall implements TemplateCall {

    private final Fragment name;
    private final Parameter[] parameters;
    private final Map<String,Parameter> parametersMap;

    public DefaultTemplateCall(Fragment name, Parameter[] params) {
        this.name = name;
        this.parameters = params;
        this.parametersMap = new HashMap<>();
        for(Parameter param : params) {
            parametersMap.put(param.name, param);
        }
    }

    @Override
    public Fragment getName() {
        return name;
    }

    @Override
    public String[] getParameters() {
        return parametersMap.keySet().toArray( new String[0] );
    }

    @Override
    public Parameter getParameter(String param) {
        return parametersMap.get(param);
    }

    @Override
    public Fragment getParameter(int index) {
        if(index >= parameters.length) return null;
        return parameters[index].value;
    }

    @Override
    public String getProcessedParameter(int index, EvaluationContext context) throws TemplateProcessorException {
        final Fragment v = getParameter(index);
        return v == null ? null : v.evaluate(context);
    }

    @Override
    public int getParametersCount() {
        return parametersMap.size();
    }

    @Override
    public Map<String,String> getParameters(int fromIndex, EvaluationContext context, boolean nullKeys)
    throws TemplateProcessorException {
        final Map<String,String> parametersMap = new HashMap<>();
        Parameter parameter;
        String k, v;
        for(int i = fromIndex; i < parameters.length; i++) {
            parameter = parameters[i];
            k = parameter.name;
            v = parameter.value.evaluate(context);
            if(!nullKeys && k == null) {
                k = v;
                v = null;
            }
            parametersMap.put(k, v);
        }
        return parametersMap;
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", this.name, this.parametersMap.toString());
    }

}
