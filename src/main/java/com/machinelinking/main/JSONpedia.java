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

package com.machinelinking.main;

import com.machinelinking.enricher.WikiEnricher;
import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.filter.DefaultJSONFilterEngine;
import com.machinelinking.filter.DefaultJSONFilterFactory;
import com.machinelinking.filter.JSONFilter;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.render.DefaultDocumentContext;
import com.machinelinking.render.DefaultHTMLRenderFactory;
import com.machinelinking.render.DocumentContext;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.template.RenderScope;
import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.util.TokenBuffer;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Main library facade.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONpedia {

    private static JSONpedia instance;

    public static JSONpedia instance() {
        if(instance == null) {
            instance = new JSONpedia();
        }
        return instance;
    }

    private JSONpedia() {}

    public Output process(String entity) throws JSONpediaException {
        return new Output(new Params(entity));
    }

    private ExecutionResult execute(Params params) throws JSONpediaException {
        try {
            final URL documentURL = JSONUtils.toResourceURL(params.entity);
            final DocumentSource documentSource = params.text ==
                    null ? new DocumentSource(documentURL) : new DocumentSource(documentURL, params.text);
            final WikiEnricher wikiEnricher = WikiEnricherFactory.getInstance()
                    .createFullyConfiguredInstance(params.flags, WikiEnricherFactory.DEFAULT_FLAGS);
            final JSONSerializer jsonSerializer;
            final JSONFilter filter;
            try {
                filter = params.filter == null ?
                        DefaultJSONFilterFactory.EMPTY_FILTER : params.filter;
            } catch (Exception e) {
                throw new IllegalArgumentException("Error while parsing filter.", e);
            }

            final TokenBuffer buffer = JSONUtils.createJSONBuffer();
            try {
                jsonSerializer = new JSONSerializer(buffer);
            } catch (IOException ioe) {
                throw new IllegalStateException("Error while initializing serializer.", ioe);
            }

            wikiEnricher.enrichEntity(documentSource, jsonSerializer);
            final TokenBuffer filtered = JSONUtils.createResultFilteredObject(buffer, filter);
            final JsonNode filteredJSON = JSONUtils.bufferToJSONNode(filtered);
            return new ExecutionResult(documentURL, filteredJSON);
        } catch (Exception e) {
            throw new JSONpediaException("Error while processing entity: " + params.entity, e);
        }
    }

    public class Output {
        private final Params params;

        public Output(Params params) {
            this.params = params;
        }

        public Output text(String text) {
            this.params.text = text;
            return this;
        }

        public Output flags(String flags) {
            this.params.flags = flags;
            return this;
        }

        public Output filter(JSONFilter filter) {
            this.params.filter = filter;
            return this;
        }

        public Output filter(String filter) {
            this.params.filter = DefaultJSONFilterEngine.parseFilter(filter);
            return this;
        }

        public JsonNode json() throws JSONpediaException {
            final ExecutionResult result = execute(params);
            return result.root;
        }

        public String html() throws JSONpediaException {
            try {
                final ExecutionResult result = execute(params);
                final DocumentContext context = new DefaultDocumentContext(
                        RenderScope.FULL_RENDERING,
                        result.documentURL
                );
                return DefaultHTMLRenderFactory.getInstance().createRender().renderDocument(context, result.root);
            } catch (Exception e) {
                throw new JSONpediaException("Error while rendering entity to HTML", e);
            }
        }

        public Map<String,?> map() throws JSONpediaException {
            final ExecutionResult result = execute(params);
            try {
                return JSONUtils.convertNodeToMap(result.root);
            } catch (IOException ioe) {
                throw new JSONpediaException("Error while converting root node to POJO.", ioe);
            }
        }
    }

    private class Params {
        final String entity;
        String text;
        String flags;
        JSONFilter filter;
        private Params(String entity) {
            this.entity = entity;
        }
    }

    private class ExecutionResult {
        private final URL documentURL;
        private final JsonNode root;
        ExecutionResult(URL documentURL, JsonNode root) {
            this.documentURL = documentURL;
            this.root = root;
        }
    }

}
