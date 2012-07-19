package com.machinelinking.render;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultHTMLRender implements HTMLRender {

    public static final String TYPE_ATTR = "__type";

    private static final Map<String,String> OBJECT_DIV_STYLE = new HashMap<String, String>(){{
        put("style","border: 1px solid");
    }};

    private static final Map<String,String> LIST_DIV_STYLE = new HashMap<String, String>(){{
        put("style","margin:5px");
    }};

    private final Map<String,List<NodeRender>> nodeRenders     = new HashMap<>();
    private final Map<String,KeyValueRender>   keyValueRenders = new HashMap<>();

    public void processRoot(JsonNode node, HTMLWriter writer) throws IOException {
        writer.openDocument();
        render(this, node, writer);
        writer.closeDocument();
        writer.flush();
    }

    @Override
    public void addNodeRender(String type, NodeRender render) {
        List<NodeRender> rendersPerType = nodeRenders.get(type);
        if(rendersPerType == null) {
            rendersPerType = new ArrayList<>();
            nodeRenders.put(type, rendersPerType);
        }
        rendersPerType.add(render);
    }

    @Override
    public boolean removeNodeRender(String type) {
        return nodeRenders.remove(type) != null;
    }

    @Override
    public void addKeyValueRender(String key, KeyValueRender render) {
        if(keyValueRenders.containsKey(key)) throw new IllegalArgumentException();
        keyValueRenders.put(key, render);
    }

    @Override
    public boolean removeKeyValueRender(String key) {
        return keyValueRenders.remove(key) != null;
    }

    @Override
    public boolean acceptNode(JsonNode node) {
        return true;
    }

    public void render(RootRender rootRender, JsonNode node, HTMLWriter writer) throws IOException {
        if(node == null) return;
        final JsonNode type = node.get(TYPE_ATTR);
        NodeRender targetRender = null;
        if (type != null) {
            final List<NodeRender> rendersPerType = nodeRenders.get(type.asText());
            if (rendersPerType != null) {
                for(NodeRender nodeRender : rendersPerType) {
                    if(nodeRender.acceptNode(node)) {
                        targetRender = nodeRender;
                        break;
                    }
                }
            }
        }
        if(targetRender != null) {
            targetRender.render(rootRender, node, writer);
            return;
        }

        if (node.isObject()) {
            renderObject(rootRender, node, writer);
        } else if (node.isArray()) {
            renderList(rootRender, node, writer);
        } else {
            renderPrimitive(node, writer);
        }
    }

    public void render(RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException {
        final KeyValueRender render = keyValueRenders.get(key);
        if(render != null) {
            render.render(rootRender, key, value, writer);
            return;
        }

        writer.openTag("div");
        writer.openTag("div");
        writer.openTag("i");
        writer.text(key);
        writer.closeTag();
        writer.text(":");
        writer.closeTag();
        writer.openTag("div");
        render(rootRender, value, writer);
        writer.closeTag();
        writer.closeTag();
    }

    private void renderObject(RootRender rootRender, JsonNode obj, HTMLWriter writer) throws IOException {
        writer.openTag("div");
        final JsonNode type = obj.get(TYPE_ATTR);
        if(type != null) {
            writer.openColorTag("red");
            writer.openTag("small");
            writer.text(type.asText());
            writer.closeTag();
            writer.closeTag();
        }
        final Iterator<Map.Entry<String,JsonNode>> iter = obj.getFields();
        Map.Entry<String,JsonNode> entry;
        while(iter.hasNext()) {
            entry = iter.next();
            if(TYPE_ATTR.equals(entry.getKey())) continue;
            writer.openTag("div", OBJECT_DIV_STYLE);
            render(rootRender, entry.getKey().trim(), entry.getValue(), writer);
            writer.closeTag();
        }
        writer.closeTag();
    }

    private void renderList(RootRender rootRender, JsonNode list, HTMLWriter writer) throws IOException {
        if(list.size() == 1) {
            render(rootRender, list.get(0), writer);
            return;
        }

        //writer.openTag("ul");
        writer.openTag("div", LIST_DIV_STYLE);
        for(int i = 0; i < list.size(); i++) {
            // writer.openTag("li");
            writer.openTag("div");
            render(rootRender, list.get(i), writer);
            writer.closeTag();
        }
        writer.closeTag();
    }

    private void renderPrimitive(JsonNode node, HTMLWriter writer) throws IOException {
        final String primitive = JSONUtils.asPrimitiveString(node);
        if (primitive != null) {
            writer.openTag("pre");
            writer.text(primitive.trim());
            writer.closeTag();
        }
    }

}
