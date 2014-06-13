package com.machinelinking.template;

import com.machinelinking.render.HTMLWriter;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TemplateProcessor {

    public void process(EvaluationContext context, TemplateCall call, HTMLWriter writer)
    throws TemplateProcessorException;

    void addTemplateCallHandler(String scope, TemplateCallHandler handler);

    void removeTemplateCallHandler(String scope, TemplateCallHandler handler);

}
