package com.machinelinking.template;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface RenderScope {

    /**
     * Renders everything even with default rendering.
     */
    static final String FULL_RENDERING = "full_rendering";

    /**
     * Renders only with renders for text rendering.
     */
    static final String TEXT_RENDERING = "text_rendering";

}
