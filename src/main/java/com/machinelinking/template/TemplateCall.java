package com.machinelinking.template;

import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TemplateCall {

    Fragment getName();

    String[] getParameters();

    Parameter getParameter(String param);

    Fragment getParameter(int index);

    String getProcessedParameter(int index, EvaluationContext context) throws TemplateProcessorException;

    int getParametersCount();

    Map<String,String> getParameters(int fromIndex, EvaluationContext context, boolean nullKeys)
    throws TemplateProcessorException;

    public class Parameter {
        public final String name;
        public final Fragment value;
        public Parameter(String name, Fragment value) {
            this.name = name;
            this.value = value;
        }
        @Override
        public String toString() {
            return String.format("%s:%s", name, value);
        }
    }

}
