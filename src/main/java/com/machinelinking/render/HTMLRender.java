package com.machinelinking.render;

/**
 * Defines a <i>JSON</i> to <i>HTML</i> renderer.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface HTMLRender extends RootRender {

    /**
     * Registers a {@link NodeRender} for a specific type.
     *
     * @param type
     * @param render
     */
    void addNodeRender(String type, NodeRender render);

    /**
     * Deregisters a {@link NodeRender} for a specific type.
     *
     * @param type
     * @return
     */
    boolean removeNodeRender(String type);

    /**
     * Registers a {@link KeyValueRender} for a given key.
     *
     * @param key
     * @param render
     */
    void addKeyValueRender(String key, KeyValueRender render);

    /**
     * Deregisters a {@link KeyValueRender} for a given key.
     *
     * @param key
     * @return
     */
    boolean removeKeyValueRender(String key);

    /**
     * Registers a {@link PrimitiveNodeRender}.
     *
     * @param render
     */
    void addPrimitiveRender(PrimitiveNodeRender render);

    /**
     * Deregisters a {@link PrimitiveNodeRender}.
     *
     * @param render
     */
    void removePrimitiveRender(PrimitiveNodeRender render);


}
