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

package com.machinelinking.render;

import com.machinelinking.util.DefaultJsonPathBuilder;
import com.machinelinking.util.JsonPathBuilder;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class BaseTextPrimitiveNodeRender implements PrimitiveNodeRender {

    private final Map<String,String> TEXT_SPAN_ATTR = new HashMap<String,String>(){{
        put("class", "primitive");
    }};

    private final JsonPathBuilder targetFilter;

    public BaseTextPrimitiveNodeRender() {
        targetFilter = new DefaultJsonPathBuilder();
        targetFilter.startPath();
        targetFilter.enterObject();
        targetFilter.field("wikitext-json");
        targetFilter.enterArray();
        targetFilter.arrayElem();
        targetFilter.enterObject();
        targetFilter.field("structure");
        targetFilter.enterArray();
    }

    @Override
    public boolean render(JsonContext context, JsonNode node, HTMLWriter writer) throws IOException {
        if(context.subPathOf(targetFilter, true) && node.isTextual()) {
            writer.openTag("span", TEXT_SPAN_ATTR);
            writer.text(node.asText());
            writer.closeTag();
            return true;
        }
        return false;
    }

}
