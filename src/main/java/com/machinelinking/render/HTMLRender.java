package com.machinelinking.render;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface HTMLRender extends RootRender {

    void addNodeRender(String type, NodeRender render);

    boolean removeNodeRender(String type);

    void addKeyValueRender(String key, KeyValueRender render);

    boolean removeKeyValueRender(String key);

    void addPrimitiveRender(PrimitiveNodeRender render);

    void removePrimitiveRender(PrimitiveNodeRender render);


}
