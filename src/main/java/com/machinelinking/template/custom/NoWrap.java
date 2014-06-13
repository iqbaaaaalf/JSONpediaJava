package com.machinelinking.template.custom;

import com.machinelinking.render.HTMLWriter;
import com.machinelinking.template.EvaluationContext;
import com.machinelinking.template.TemplateCall;
import com.machinelinking.template.TemplateCallHandler;
import com.machinelinking.template.TemplateCallHandlerException;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NoWrap implements TemplateCallHandler {

    public static final String NOWRAP_TEMPLATE_NAME = "nowrap";

    @Override
    public boolean process(EvaluationContext context, TemplateCall call, HTMLWriter writer)
    throws TemplateCallHandlerException {
        if(!NOWRAP_TEMPLATE_NAME.equals(context.evaluate(call.getName()))) return false;
        try {
            for (TemplateCall.Parameter p : call.getParameters()) {
                writer.text(context.evaluate(p.value));
            }
            return true;
        } catch (IOException ioe) {
            throw new TemplateCallHandlerException("Error while processing nowrap template", ioe);
        }
    }
}
