package com.machinelinking.template;

import com.machinelinking.render.HTMLWriter;

/**
 * @see com.machinelinking.template.TemplateProcessor
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TemplateCallHandler {

    boolean process(EvaluationContext context, TemplateCall call, HTMLWriter writer) throws TemplateCallHandlerException;

}
