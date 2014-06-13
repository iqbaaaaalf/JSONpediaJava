package com.machinelinking.template.custom;

import com.machinelinking.render.HTMLWriter;
import com.machinelinking.template.EvaluationContext;
import com.machinelinking.template.TemplateCall;
import com.machinelinking.template.TemplateCallHandler;
import com.machinelinking.template.TemplateCallHandlerException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateSuppressor implements TemplateCallHandler {

    private final String[] namePatterns;

    public TemplateSuppressor(String... namePatterns) {
        this.namePatterns = namePatterns;
    }

    @Override
    public boolean process(EvaluationContext context, TemplateCall call, HTMLWriter writer)
    throws TemplateCallHandlerException {
        final String name = context.evaluate(call.getName());
        for(String namePattern : namePatterns) {
            if(name.matches(namePattern)) return true;
        }
        return false;
    }

}
