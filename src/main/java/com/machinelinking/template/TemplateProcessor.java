package com.machinelinking.template;

import com.machinelinking.render.HTMLWriter;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TemplateProcessor {

    public void process(EvaluationContext context, TemplateCall call, HTMLWriter writer)
    throws TemplateProcessorException;

    void addTemplateCallHandler(TemplateCallHandler handler);

    void removeTemplateCallHandler(TemplateCallHandler handler);

}
