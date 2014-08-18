/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

package com.machinelinking.filter;

import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Default {@link JSONFilterEngine} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONFilterEngine implements JSONFilterEngine {

    public static JSONFilter parseFilter(String exp, JSONFilterFactory factory) {
        final JSONFilterParser parser = new DefaultJSONFilterParser();
        return parser.parse(exp, factory);
    }

    public static JSONFilter parseFilter(String exp) {
        return parseFilter(exp, new DefaultJSONFilterFactory());
    }

    public static JsonNode[] applyFilter(JsonNode node, String exp) {
        final JSONFilter filter = parseFilter(exp);
        final JSONFilterEngine engine = new DefaultJSONFilterEngine();
        return engine.filter(node, filter);
    }

    public static JsonNode[] applyFilter(JsonNode node, JSONFilter filter) {
        if(filter.isEmpty()) return new JsonNode[]{node};
        final JSONFilterEngine engine = new DefaultJSONFilterEngine();
        return engine.filter(node, filter);
    }

    @Override
    public JsonNode[] filter(JsonNode in, JSONFilter filter) {
        final List<JsonNode> result = new ArrayList<>();
        filterNode(in, filter, result);
        return result.toArray( new JsonNode[result.size()] );
    }

    private List<JsonNode> selectNode(JsonNode t, JSONFilter f) {
        final List<JsonNode> r = new ArrayList<>();
        if(f instanceof JSONKeyFilter) {
            final JSONKeyFilter keyFilter = (JSONKeyFilter) f;
            final Iterator<Map.Entry<String,JsonNode>> entries = t.getFields();
            while(entries.hasNext()) {
                final Map.Entry<String,JsonNode> entry = entries.next();
                if(keyFilter.matchKey(entry.getKey())) {
                    r.add(entry.getValue());
                }
            }
        } else if(f instanceof JSONObjectFilter) {
            final JSONObjectFilter objectFilter = (JSONObjectFilter) f;
            if(objectFilter.match(t)) {
                r.add(t);
            }
        }
        return r;
    }

    private void filterNode(JsonNode n, JSONFilter f, List<JsonNode> r) {
        if(n.isObject()) {
            final List<JsonNode> matched = selectNode(n, f);
            if(!matched.isEmpty()) {
                final JSONFilter nested = f.getNested();
                if(nested == null) {
                    r.addAll(matched);
                } else {
                    final List<JsonNode> candidate = new ArrayList<>();
                    for(JsonNode m : matched) {
                        filterObject(m, nested, candidate);
                    }
                    r.addAll(candidate);
                }
            } else {
                filterObject(n, f, r);
            }
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
