package com.machinelinking.template;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateProcessorException extends Exception {

    private final Fragment fragment;

    public TemplateProcessorException(String message, Fragment fragment) {
        super(message);
        this.fragment = fragment;
    }

    public TemplateProcessorException(String message, Throwable cause, Fragment fragment) {
        super(message, cause);
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

}
