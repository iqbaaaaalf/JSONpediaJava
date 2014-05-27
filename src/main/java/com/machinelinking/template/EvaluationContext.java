package com.machinelinking.template;

import com.machinelinking.pagestruct.PageStructConsts;
import com.machinelinking.render.DefaultHTMLWriter;
import com.machinelinking.render.HTMLWriter;
import com.machinelinking.render.JsonContext;
import com.machinelinking.render.RootRender;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class EvaluationContext {

    private final RootRender rootRender;
    private final JsonContext jsonContext;

    public EvaluationContext(RootRender rootRender, JsonContext jsonContext) {
        this.rootRender = rootRender;
        this.jsonContext = jsonContext;
    }

    public JsonContext getJsonContext() {
        return jsonContext;
    }

    public String getVarValue(String name) {
        return jsonContext.getDocumentContext().getVar(name);
    }

    public void evaluate(JsonNode node, StringBuilder sb) {
        if(node.isArray()) {
            evaluate((ArrayNode) node, sb);
        } else if(node.isObject()) {
            evaluate((ObjectNode) node, sb);
        } else {
            sb.append(node.asText());
        }
    }

    public String evaluate(JsonNode node) {
        if(node == null || node.isNull()) return null;
        final StringBuilder sb = new StringBuilder();
        evaluate(node, sb);
        return sb.toString();
    }

    public Map<String,String> evaluate(TemplateCall.Parameter[] parameters, int from) {
        final Map<String,String> result = new HashMap<>();
        String name, value;
        for (int i = from; i < parameters.length; i++) {
            name = parameters[i].name;
            value = evaluate(parameters[i].value);
            if(name == null) {
                name = value;
                value = null;
            }
            result.put(name, value);
        }
        return result;
    }

    public void evaluate(String field, JsonNode value, HTMLWriter writer) throws IOException {
        rootRender.render(this.jsonContext, rootRender, field, value, writer);
    }

    private void evaluate(ArrayNode array, StringBuilder sb) {
        for(JsonNode elem : array) {
            evaluate(elem, sb);
        }
    }

    private void evaluate(ObjectNode obj, StringBuilder sb) {
        final String type = obj.get(PageStructConsts.TYPE_FIELD).asText();
        if(PageStructConsts.TYPE_VAR.equals(type)) {
            final String name = obj.get(PageStructConsts.NAME_FIELD).asText();
            final JsonNode defaultValue = obj.get(PageStructConsts.DEFAULT_FIELD);
            final String varValue = getVarValue(name);
            final String value = varValue != null ? varValue : evaluate(defaultValue);
            if(value != null) sb.append(value);
        } else {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final OutputStreamWriter osw = new OutputStreamWriter(baos);
            final HTMLWriter writer = new DefaultHTMLWriter(osw);
            try {
                rootRender.render(jsonContext, rootRender, obj, writer);
                writer.flush();
                sb.append(baos.toString());
            } catch (IOException ioe) {
                throw new RuntimeException(ioe); //TODO: improve exc management.
            }
        }
    }

}
