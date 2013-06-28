package com.machinelinking.filter;

import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Default {@link JSONFilterEngine} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONFilterEngine implements JSONFilterEngine {

    public static JSONFilter parseFilter(String exp) {
        final JSONFilter filter = new DefaultJSONFilter();
        final JSONFilterParser parser = new DefaultJSONFilterParser();
        parser.parse(exp, filter);
        return filter;
    }

    public static JsonNode[] applyFilter(JsonNode node, String exp) {
        final JSONFilter filter = parseFilter(exp);
        final JSONFilterEngine engine = new DefaultJSONFilterEngine();
        return engine.filter(node, filter);
    }

    public static JsonNode[] applyFilter(JsonNode node, JSONFilter filter) {
        final JSONFilterEngine engine = new DefaultJSONFilterEngine();
        return engine.filter(node, filter);
    }

    @Override
    public JsonNode[] filter(JsonNode in, JSONFilter filter) {
        final List<JsonNode> result = new ArrayList<>();
        filterNode(in, filter, result);
        return result.toArray( new JsonNode[result.size()] );
    }

    private void filterNode(JsonNode n, JSONFilter f, List<JsonNode> r) {
        if(n.isObject()) {
            if(f.match(n)) r.add(n);
            filterObject(n, f, r);
        } else if(n.isArray()) {
            filterArray(n, f, r);
        }
    }

    private void filterObject(JsonNode n, JSONFilter f, List<JsonNode> r) {
        final Iterator<JsonNode> elements = n.getElements();
        while (elements.hasNext()) {
            filterNode(elements.next(), f, r);
        }
    }

    private void filterArray(JsonNode n, JSONFilter f, List<JsonNode> r) {
        final int size = n.size();
        for(int i = 0; i < size; i++) {
            filterNode(n.get(i), f, r);
        }
    }

}
