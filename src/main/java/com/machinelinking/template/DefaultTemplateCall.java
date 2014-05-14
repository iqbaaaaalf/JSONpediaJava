package com.machinelinking.template;

import com.machinelinking.parser.WikiTextParserHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class  DefaultTemplateCall implements TemplateCall {

    private final Value name;
    private final Parameter[] parameters;
    private final Map<String,Parameter> parametersMap;

    public DefaultTemplateCall(Value name, Parameter[] params) {
        this.name = name;
        this.parameters = params;
        this.parametersMap = new HashMap<>();
        for(Parameter param : params) {
            parametersMap.put(param.name, param);
        }
    }

    @Override
    public Value getName() {
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
    public Value getParameter(int index) {
        if(index >= parameters.length) return null;
        return parameters[index].value;
    }

    @Override
    public String getProcessedParameter(int index, EvaluationContext context) throws TemplateProcessorException {
        final Value v = getParameter(index);
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

    interface Fragment extends Value {}

    static class ConstFragment implements Fragment {
        final String constValue;
        ConstFragment(String constValue) {
            this.constValue = constValue;
        }
        @Override
        public String evaluate(EvaluationContext context) {
            return constValue;
        }
        @Override
        public String toString() {
            return String.format("'%s'", constValue);
        }
    }

    static class VariableFragment implements Fragment {
        final WikiTextParserHandler.Var var;
        VariableFragment(WikiTextParserHandler.Var var) {
            this.var = var;
        }
        @Override
        public String evaluate(EvaluationContext context) {
            return context.getValue(var);
        }
        @Override
        public String toString() {
            return String.format("<%s>", var);
        }
    }

    static class TemplateFragment implements Fragment {
        final TemplateCall call;
        TemplateFragment(TemplateCall call) {
            this.call = call;
        }
        @Override
        public String evaluate(EvaluationContext context) throws TemplateProcessorException {
            return context.getValue(call);
        }
        @Override
        public String toString() {
            return String.format("(%s)", call);
        }
    }

    static public class DefaultValue implements Value {
        private Fragment[] fragments;

        public DefaultValue(Fragment[] fragments) {
            this.fragments = fragments;
        }
        @Override
        public String evaluate(EvaluationContext context) throws TemplateProcessorException {
            final StringBuilder builder = new StringBuilder();
            for(Fragment fragment : fragments) {
                builder.append(fragment.evaluate(context));
            }
            return builder.toString();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append('[');
            for(Fragment fragment : fragments) {
                sb.append(fragment.toString());
            }
            sb.append(']');
            return sb.toString();
        }
    }



}
