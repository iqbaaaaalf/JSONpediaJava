package com.machinelinking.parser;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextParserSuspendableHandler extends WikiTextParserFilteredHandler {

    private boolean enabled = true;

    public WikiTextParserSuspendableHandler(WikiTextParserHandler decorated) {
        super(decorated, null);
    }

    public void sedEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected boolean mustFilter() {
        return !enabled;
    }

}
