/*
 * Copyright 2012-2015 Michele Mostarda (me@michelemostarda.it)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.machinelinking.render;

import com.machinelinking.pagestruct.Ontology;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ListNodeRender implements NodeRender {

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws NodeRenderException {
        final JsonNode content = node.get(Ontology.CONTENT_FIELD);
        if(content == null || content.isNull()) return;
        try {
            writer.openList();
            for (JsonNode item : content) {
                writer.openListItem();
                rootRender.render(context, rootRender, item.get(Ontology.CONTENT_FIELD), writer);
                writer.closeListItem();
            }
            writer.closeList();
        } catch (IOException ioe) {
            throw new NodeRenderException(ioe);
        }
    }
}
