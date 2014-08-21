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

package com.machinelinking.extractor;

import com.machinelinking.freebase.FreebaseService;
import com.machinelinking.pagestruct.Ontology;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.util.JSONUtils;
import com.machinelinking.wikimedia.WikimediaUtils;
import org.codehaus.jackson.JsonNode;

import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <i>Freebase</i> {@link Extractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FreebaseExtractor extends Extractor {

    private Future<JsonNode> entityFetching;

    public FreebaseExtractor() {
        super(Ontology.FREEBASE_FIELD);
    }

    @Override
    public void beginDocument(URL document) {
        if(entityFetching != null) throw new IllegalStateException();
        final String entityName = WikimediaUtils.getEntityName(document.toExternalForm());
        entityFetching = fetchEntityData(entityName);
    }

    @Override
    public void flushContent(Serializer serializer) {
        try {
            final JsonNode freebaseData = entityFetching.get();
            JSONUtils.jacksonNodeToSerializer(freebaseData, serializer);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching entity.", e);
        }
    }

    @Override
    public void reset() {
        if(entityFetching != null) {
            entityFetching.cancel(true);
            entityFetching = null;
        }
    }

    private Future<JsonNode> fetchEntityData(final String entityName) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        return executorService.submit( new Callable<JsonNode>() {
            @Override
            public JsonNode call() throws Exception {
                return FreebaseService.getInstance().getEntityData(entityName);
            }
        });
    }

}
