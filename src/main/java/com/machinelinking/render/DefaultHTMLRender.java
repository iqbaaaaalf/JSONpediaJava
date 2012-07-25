package com.machinelinking.render;

import com.machinelinking.util.DefaultJsonPathBuilder;
import com.machinelinking.util.JSONUtils;
import com.machinelinking.util.JsonPathBuilder;
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

    private static final Map<String,String> PRIMITIVE_NODE_ATTR = new HashMap<String,String>(){{
        put("style", "background-color: white");
    }};

    private static final String JSON_PATH_SELECTOR      = "jsonpath";
    private static final String DEFAULT_RENDER_SELECTOR = "defaultrender";

    private static final Map<String,String> OBJECT_DIV_STYLE = new HashMap<String, String>(){{
        put("style","border: 1px solid");
    }};

    private static final Map<String,String> LIST_DIV_STYLE = new HashMap<String, String>(){{
        put("style","margin:5px");
    }};

    private static final Map<String,String> JSON_PATH_CLASS = new HashMap<String, String>(){{
        put("class", JSON_PATH_SELECTOR);
    }};

    private static String DEFAULT_RENDER_BG_COLOR = "background-color: #8A3737;";

    private static final Map<String,String> DEFAULT_RENDER_DIV = new HashMap<String,String>(){{
        put("style", DEFAULT_RENDER_BG_COLOR);
    }};

    private static final Map<String,String> DEFAULT_RENDER_HIDDEN_DIV = new HashMap<String,String>(){{
        put("class", DEFAULT_RENDER_SELECTOR);
        put("style", DEFAULT_RENDER_BG_COLOR + "visibility:none");
    }};

    private final Map<String,List<NodeRender>> nodeRenders     = new HashMap<>();
    private final Map<String,KeyValueRender>   keyValueRenders = new HashMap<>();

    private final JsonPathBuilder jsonPathBuilder = new DefaultJsonPathBuilder();

    public void processRoot(JsonNode node, HTMLWriter writer) throws IOException {
        jsonPathBuilder.startPath();
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
        final JsonNode type = node.get(TemplateConstants.TYPE_ATTR);
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

        writeNodeMetadata(node, writer);

        if(targetRender != null) {
            targetRender.render(rootRender, node, writer);
            writer.openTag("div", DEFAULT_RENDER_HIDDEN_DIV);
        } else {
            writer.openTag("div", DEFAULT_RENDER_DIV);
        }

        if (node.isObject()) {
            renderObject(rootRender, node, writer);
        } else if (node.isArray()) {
            renderList(rootRender, node, writer);
        } else {
            renderPrimitive(node, writer);
        }

        writer.closeTag();

        writer.closeTag();
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
        jsonPathBuilder.enterObject();
        writer.openTag("div");
        final Iterator<Map.Entry<String,JsonNode>> iter = obj.getFields();
        Map.Entry<String,JsonNode> entry;
        while(iter.hasNext()) {
            entry = iter.next();
            if(TemplateConstants.TYPE_ATTR.equals(entry.getKey())) continue;
            writer.openTag("div", OBJECT_DIV_STYLE);
            jsonPathBuilder.field(entry.getKey());
            render(rootRender, entry.getKey().trim(), entry.getValue(), writer);
            writer.closeTag();
        }
        jsonPathBuilder.exitObject();

        writer.closeTag();
    }

    private void renderList(RootRender rootRender, JsonNode list, HTMLWriter writer) throws IOException {
        final int size = list.size();
        if(size == 1) {
            render(rootRender, list.get(0), writer);
            return;
        } else if(size == 0) {
            renderPrimitive(list, writer);
            return;
        }

        writer.openTag("div", LIST_DIV_STYLE);
        jsonPathBuilder.enterArray();
        for(int i = 0; i < list.size(); i++) {
            jsonPathBuilder.arrayElem();
            writer.openTag("div");
            render(rootRender, list.get(i), writer);
            writer.closeTag();
        }
        jsonPathBuilder.exitArray();
        writer.closeTag();
    }


    private void renderPrimitive(JsonNode node, HTMLWriter writer) throws IOException {
        final String primitive = JSONUtils.asPrimitiveString(node);
        if (primitive != null) {
            writer.openTag("pre", PRIMITIVE_NODE_ATTR);
            writer.text(primitive.trim());
            writer.closeTag();
        } else {
            writer.openTag("pre");
            writer.text("&lt;empty&gt;");
            writer.closeTag();
        }
    }

    // TODO: should write any metadata available
    private String writeNodeMetadata(JsonNode node, HTMLWriter writer) throws IOException {
        final JsonNode typeNode = node.get(TemplateConstants.TYPE_ATTR);
        final String type;
        if(typeNode == null) {
            type = null;
            writer.openTag("div");
        } else {
            type = typeNode.asText();
            final String name;
            name = "template".equals(type) ? node.get("name").asText() : null;
            writer.openTag("div", new HashMap<String, String>(){{
                put("itemtype", type);
                put("name"    , name);
            }});

            writer.openColorTag("red");
            writer.openTag("small");
            writer.text(type);
            writer.closeTag();
            writer.closeTag();
        }


        writer.openTag("small", JSON_PATH_CLASS);
        writer.text("(");
        writer.text(jsonPathBuilder.getJsonPath());
        writer.text(")");
        writer.closeTag();

        return type;
    }

}
