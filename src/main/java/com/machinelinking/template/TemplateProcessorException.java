package com.machinelinking.template;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateProcessorException extends Exception {

    private final TemplateCall call;

    public TemplateProcessorException(String message, TemplateCall call) {
        super(message);
        this.call = call;
    }

    public TemplateProcessorException(String message, Exception e, TemplateCall call) {
        super(message, e);
        this.call = call;
    }

    public TemplateCall getCall() {
        return call;
    }

    @Override
    public String toString() {
        return String.format("%s\n%s", super.toString(), call);
    }

}
