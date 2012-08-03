package com.machinelinking.extractor;

import com.machinelinking.pagestruct.TemplateMapping;
import com.machinelinking.parser.WikiPediaUtils;
import com.machinelinking.serializer.Serializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateMappingExtractor extends Extractor {

    private final ExecutorService executorService               = Executors.newCachedThreadPool();
    private final List<Future> waitList                         = new ArrayList<>();
    private final Map<String,TemplateMapping> collectedMappings = new HashMap<>();

    public TemplateMappingExtractor() {
        super("template-mapping");
    }

    public Map<String,TemplateMapping> getCollectedMappings() throws ExecutionException, InterruptedException {
        waitMappings();
        return Collections.unmodifiableMap(collectedMappings);
    }

    @Override
    public void beginTemplate(String name) {
        if(WikiPediaUtils.getInfoBoxName(name) != null) {
            fetchMapping(name.trim());
        }
    }

    @Override
    public void flushContent(Serializer serializer) {
        try {
            waitMappings();
        } catch (Exception e) {
            throw new RuntimeException("Error while waiting for mappings.", e);
        }
        writeEntityMappingJSONSerialization(collectedMappings, serializer);
    }

    @Override
    public void reset() {
        clearWaitList();
        collectedMappings.clear();
    }

    private void fetchMapping(final String mappingName) {
        final Future future = executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final TemplateMapping mapping = TemplateMapping.readMappingForTemplate(mappingName);
                            if(mapping != null) collectedMappings.put(mappingName, mapping);
                        } catch (Exception e) {
                            throw new RuntimeException("Error while fetching mapping " + mappingName, e);
                        }
                    }
                }
        );
        waitList.add(future);
    }

    private void waitMappings() throws InterruptedException, ExecutionException {
        for (Future future : waitList) {
            future.get();
        }
    }

    private void clearWaitList() {
        for(Future future : waitList) {
            future.cancel(true);
        }
        waitList.clear();
    }

    private void writeEntityMappingJSONSerialization(
        Map<String,TemplateMapping> collectedMappings, Serializer serializer
    ) {
        serializer.openObject();
        serializer.fieldValue("type", "mapping-collection");
        serializer.field("mapping-collection");
        serializer.openList();
        for(Map.Entry<String,TemplateMapping> entry : collectedMappings.entrySet()) {
            entry.getValue().serialize(serializer);
        }
        serializer.closeList();
        serializer.closeObject();
    }

}
