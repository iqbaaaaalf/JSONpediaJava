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
        //put("style","border: 1px solid; padding 5px;");
        put("style","border: 1px solid; padding 2px;");
    }};

    private static final Map<String,String> LIST_DIV_STYLE = new HashMap<String, String>(){{
        //put("style","margin:5px; padding 5px;");
        put("style","margin:2px; padding 2px;");
    }};

    private static final Map<String,String> JSON_PATH_CLASS = new HashMap<String, String>(){{
        put("class", JSON_PATH_SELECTOR);
    }};

    private static String DEFAULT_RENDER_BG_COLOR        = "background-color: #8A3737;";
    private static String DEFAULT_RENDER_HIDDEN_BG_COLOR = "background-color: #8A5757;";

    private static final Map<String,String> DEFAULT_RENDER_DIV = new HashMap<String,String>(){{
        //put("style", DEFAULT_RENDER_BG_COLOR);
    }};

    private static final Map<String,String> DEFAULT_RENDER_HIDDEN_DIV = new HashMap<String,String>(){{
        put("class", DEFAULT_RENDER_SELECTOR);
        put("style", DEFAULT_RENDER_HIDDEN_BG_COLOR + "visibility:none");
    }};

    private final Map<String,List<NodeRender>> nodeRenders       = new HashMap<>();
    private final Map<String,KeyValueRender>   keyValueRenders   = new HashMap<>();
    private final List<PrimitiveNodeRender> primitiveNodeRenders = new ArrayList<>();

    private final JsonPathBuilder jsonPathBuilder = new DefaultJsonPathBuilder();

    private final JsonContext context = new JsonContext() {
        @Override
        public String getJSONPath() {
            return jsonPathBuilder.getJsonPath();
        }

        @Override
        public boolean subPathOf(JsonPathBuilder builder, boolean strict) {
            return jsonPathBuilder.subPathOf(builder, strict);
        }
    };

    public void processRoot(JsonNode node, HTMLWriter writer) throws IOException {
        jsonPathBuilder.startPath();
        writer.openDocument();
        render(getContext(), this, node, writer);
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
    public void addPrimitiveRender(PrimitiveNodeRender render) {
        primitiveNodeRenders.add(render);
    }

    @Override
    public void removePrimitiveRender(PrimitiveNodeRender render) {
        primitiveNodeRenders.remove(render);
    }

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer) throws IOException {
        if(node == null) return;
        final JsonNode type = node.get(TemplateConstants.TYPE_ATTR);
        NodeRender targetRender = null;
        if (type != null) {
            final List<NodeRender> rendersPerType = nodeRenders.get(type.asText());
            if (rendersPerType != null) {
                for(NodeRender nodeRender : rendersPerType) {
                    if(nodeRender.acceptNode(context, node)) {
                        targetRender = nodeRender;
                        break;
                    }
                }
            }
        }

        writeNodeMetadata(node, writer);

        // Custom render.
        if(targetRender != null) {
            targetRender.render(context, rootRender, node, writer);
        }

        // Default render.
        writer.openTag("span");
        final boolean isDefault = targetRender == null;
        if (node.isObject()) {
            renderObject(rootRender, node, isDefault, writer);
        } else if (node.isArray()) {
            renderList(rootRender, node, isDefault, writer);
        } else {
            renderPrimitive(node, writer);
        }
        writer.closeTag();

        writer.closeTag();
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException {
        final KeyValueRender render = keyValueRenders.get(key);
        if(render != null) {
            render.render(context, rootRender, key, value, writer);
            return;
        }

        writer.openTag("div");
        writer.openTag("span");
        writer.openTag("i");
        writer.text(key);
        writer.closeTag();
        writer.text(":");
        writer.closeTag();
        writer.openTag("span");
        render(context, rootRender, value, writer);
        writer.closeTag();
        writer.closeTag();
    }

    private JsonContext getContext() {
        return context;
    }

    private void renderObject(RootRender rootRender, JsonNode obj, boolean isDefault, HTMLWriter writer)
    throws IOException {
        jsonPathBuilder.enterObject();
        writer.openTag("div", isDefault ? DEFAULT_RENDER_DIV : DEFAULT_RENDER_HIDDEN_DIV);
        final Iterator<Map.Entry<String,JsonNode>> iter = obj.getFields();
        Map.Entry<String,JsonNode> entry;
        while(iter.hasNext()) {
            entry = iter.next();
            if(TemplateConstants.TYPE_ATTR.equals(entry.getKey())) continue;
            writer.openTag("div", OBJECT_DIV_STYLE);
            jsonPathBuilder.field(entry.getKey());
            render(getContext(), rootRender, entry.getKey().trim(), entry.getValue(), writer);
            writer.closeTag();
        }
        jsonPathBuilder.exitObject();

        writer.closeTag();
    }

    private void renderList(RootRender rootRender, JsonNode list, boolean isDefault, HTMLWriter writer)
      throws IOException {
        final int size = list.size();
        if(size == 1) {
            jsonPathBuilder.enterArray();
            jsonPathBuilder.arrayElem();
            render(getContext(), rootRender, list.get(0), writer);
            jsonPathBuilder.exitArray();
            return;
        } else if(size == 0) {
            renderPrimitive(list, writer);
            return;
        }

        writer.openTag("div", LIST_DIV_STYLE);
        writer.openTag("div", isDefault ? DEFAULT_RENDER_DIV : DEFAULT_RENDER_HIDDEN_DIV);
        jsonPathBuilder.enterArray();
        for(int i = 0; i < list.size(); i++) {
            jsonPathBuilder.arrayElem();
            writer.openTag("div");
            render(getContext(), rootRender, list.get(i), writer);
            writer.closeTag();
        }
        jsonPathBuilder.exitArray();
        writer.closeTag();
        writer.closeTag();
    }

    private void renderPrimitive(JsonNode node, HTMLWriter writer) throws IOException {
        for(PrimitiveNodeRender primitiveRender : primitiveNodeRenders) {
            if( primitiveRender.render(getContext(), node, writer) ) return;
        }

        final String primitive = JSONUtils.asPrimitiveString(node);
        if (primitive != null) {
            writer.openTag("span", PRIMITIVE_NODE_ATTR);
            writer.text(primitive.trim());
            writer.closeTag();
        } else {
            writer.openTag("span");
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
            writer.openTag("span");
        } else {
            type = typeNode.asText();
            final String name;
            name = "template".equals(type) ? node.get("name").asText() : null;
            writer.openTag("div", new HashMap<String, String>(){{
                put("itemtype", type);
                put("name"    , name);
            }});

            //writer.br();
            writer.openColorTag("red");
            writer.openTag("small");
            writer.text(type);
            writer.closeTag();
            writer.closeTag();
        }


        writer.openTag("small", JSON_PATH_CLASS);
        writer.em();
        writer.text("(");
        writer.text(jsonPathBuilder.getJsonPath());
        writer.text(")");
        writer.closeTag();
        writer.closeTag();

        return type;
    }

}
