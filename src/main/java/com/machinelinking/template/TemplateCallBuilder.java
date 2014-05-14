package com.machinelinking.template;

import com.machinelinking.parser.DefaultWikiTextParserHandler;
import com.machinelinking.parser.WikiTextParserHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateCallBuilder extends DefaultWikiTextParserHandler {

    public static WikiTextParserHandler createHandlerWithListener(final TemplateCallListener listener) {
        final TemplateCallBuilder builder = new TemplateCallBuilder(){
            private int nesting = 0;
            @Override
            public void beginTemplate(TemplateName name) {
                nesting++;
                super.beginTemplate(name);
            }

            @Override
            public void endTemplate(TemplateName name) {
                super.endTemplate(name);
                nesting--;
                if(nesting == 0) {
                    listener.handle( getTemplateCall() );
                }
            }
        };
        return builder;
    }

    class TemplateBuffers {
        private final List<DefaultTemplateCall.Fragment> nameBuffer = new ArrayList<>();
        private final List<String> paramNames = new ArrayList<>();
        private final LinkedList<List<DefaultTemplateCall.Fragment>> paramValuesBuffer = new LinkedList<>();

        private void addParamBuffer(String paramName) {
            paramNames.add(paramName);
            paramValuesBuffer.add(new ArrayList<DefaultTemplateCall.Fragment>());
        }

        private void addParamFragment(DefaultTemplateCall.Fragment fragment) {
            paramValuesBuffer.getLast().add(fragment);
        }
    }

    private final Stack<TemplateBuffers> levels = new Stack<>();
    private final List<DefaultTemplateCall.Fragment> resultBuffer = new ArrayList<>();

    public TemplateCallBuilder() {}

    public TemplateCall.Value getValue() {
        if(resultBuffer.size() == 0)
            throw new IllegalStateException("No events processed.");
        final TemplateCall.Value result = new DefaultTemplateCall.DefaultValue(
                resultBuffer.toArray(new DefaultTemplateCall.Fragment[resultBuffer.size()])
        );
        reset();
        return result;
    }

    public TemplateCall getTemplateCall() {
        try {
            return ((DefaultTemplateCall.TemplateFragment) resultBuffer.get(0)).call;
        } catch (Exception e) {
            throw new IllegalStateException();
        } finally {
            reset();
        }
    }

    public void reset() {
        levels.clear();
        resultBuffer.clear();
    }

    @Override
    public void beginTemplate(TemplateName name) {
        final TemplateBuffers te = pushTemplate();
        for(Value v : name.fragments) {
            if(v instanceof Var) {
                te.nameBuffer.add(new DefaultTemplateCall.VariableFragment((Var)v));
            } else if(v instanceof Const) {
                te.nameBuffer.add(new DefaultTemplateCall.ConstFragment(((Const)v).constValue));
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public void parameter(String param) {
        final TemplateBuffers te = peekTemplate();
        te.addParamBuffer(param);
    }

    @Override
    public void text(String content) {
        final DefaultTemplateCall.Fragment fragment = new DefaultTemplateCall.ConstFragment(content);
        final TemplateBuffers te = peekTemplate();
        if(te == null) {
            resultBuffer.add(fragment);
        } else {
            te.addParamFragment(fragment);
        }
    }

    @Override
    public void var(Var v) {
        final DefaultTemplateCall.Fragment fragment = new DefaultTemplateCall.VariableFragment(v);
        final TemplateBuffers te = peekTemplate();
        if(te == null) {
            resultBuffer.add(fragment);
        } else {
            te.addParamFragment(new DefaultTemplateCall.VariableFragment(v));
        }
    }

    @Override
    public void endTemplate(TemplateName name) {
        final TemplateBuffers te = popTemplate();
        final List<TemplateCall.Parameter> parameters = new ArrayList<>();
        int i = 0;
        for(List<DefaultTemplateCall.Fragment> paramValueBuffer : te.paramValuesBuffer) {
            parameters.add(
                    new TemplateCall.Parameter(
                            te.paramNames.get(i++),
                            new DefaultTemplateCall.DefaultValue(
                                    paramValueBuffer.toArray(new DefaultTemplateCall.Fragment[paramValueBuffer.size()])
                            )
                    )
            );
        }
        final TemplateCall templateCall = new DefaultTemplateCall(
                new DefaultTemplateCall.DefaultValue(
                        te.nameBuffer.toArray(new DefaultTemplateCall.Fragment[te.nameBuffer.size()])
                ),
                parameters.toArray(new TemplateCall.Parameter[parameters.size()])
        );
        final DefaultTemplateCall.Fragment fragment = new DefaultTemplateCall.TemplateFragment(templateCall);
        final TemplateBuffers parent = peekTemplate();
        if(parent == null) {
            resultBuffer.add(fragment);
        } else {
            parent.addParamFragment(fragment);
        }
    }

    private TemplateBuffers pushTemplate() {
        final TemplateBuffers te = new TemplateBuffers();
        levels.push(te);
        return te;
    }

    private TemplateBuffers peekTemplate() {
        if(levels.isEmpty()) return null;
        return levels.peek();
    }

    private TemplateBuffers popTemplate() {
        return levels.pop();
    }

}
