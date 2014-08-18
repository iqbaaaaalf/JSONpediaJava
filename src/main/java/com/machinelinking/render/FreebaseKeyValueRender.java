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

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FreebaseKeyValueRender implements KeyValueRender {

    private static final Map<String,String> TYPE_DIV_ATTR = new HashMap<String,String>(){{
        put("class", "freebase");
    }};

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException {
        writer.openTag("div");

        writer.heading(3, "Freebase");

        final String id   = value.get("id").asText();
        final String mid = value.get("mid").asText();
        final String name = value.get("name").asText();
        writer.openTag("pre");
        writer.text(name);
        writer.closeTag();

        writer.openTag("pre");
        writer.text(mid);
        writer.closeTag();

        writer.anchor(
                String.format("http://www.freebase.com/view/%s", id),
                name,
                false
        );

        writer.heading(4, "Types");
        final JsonNode notable = value.get("notable");
        writer.openTag("span", TYPE_DIV_ATTR);
        writer.anchor(
                String.format("http://www.freebase.com/view/%s", notable.get("id").asText()),
                notable.get("name").asText(),
                false
        );
        writer.closeTag();

        writer.closeTag();
    }
}
