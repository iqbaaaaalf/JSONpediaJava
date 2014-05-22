package com.machinelinking.template;

/**
 * Defines a listener of template calls.
 *
 * @see com.machinelinking.template.TemplateCall
 * @see com.machinelinking.template.TemplateCallBuilder
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TemplateCallListener {

    void handle(TemplateCall call);

}
