package com.machinelinking.template;

import com.machinelinking.parser.WikiTextParserHandler;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Fragment {

    String evaluate(EvaluationContext context) throws TemplateProcessorException;

    public class CompositeFragment implements Fragment {
        private Fragment[] fragments;
        public CompositeFragment(Fragment[] fragments) {
            this.fragments = fragments;
        }
        @Override
        public String evaluate(EvaluationContext context) throws TemplateProcessorException {
            final StringBuilder builder = new StringBuilder();
            for (Fragment fragment : fragments) {
                builder.append(fragment.evaluate(context));
            }
            return builder.toString();
        }
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (Fragment fragment : fragments) {
                sb.append(fragment.toString());
            }
            sb.append(']');
            return sb.toString();
        }
    }

    class ConstFragment implements Fragment {
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

}
