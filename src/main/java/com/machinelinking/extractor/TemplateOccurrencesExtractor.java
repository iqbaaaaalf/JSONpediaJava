package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateOccurrencesExtractor extends Extractor {

    private Map<String,Integer> templateOccurrences = new HashMap<>();

    public TemplateOccurrencesExtractor() {
        super("template-occurrences");
    }

    @Override
    public void beginTemplate(String name) {
        name = name.trim();
        Integer count = templateOccurrences.get(name);
        count = count == null ? 1 : count + 1;
        templateOccurrences.put(name, count);
    }

    @Override
    public void flushContent(Serializer serializer) {
        serializer.openObject();
        serializer.fieldValue("__type", "template_occurrences");
        serializer.field("occurrences");
        serializer.openObject();
        for(Map.Entry<String,Integer> entry : templateOccurrences.entrySet()) {
            serializer.fieldValue(entry.getKey(), entry.getValue());
        }
        serializer.closeObject();
        serializer.closeObject();
    }

    @Override
    public void reset() {
        templateOccurrences.clear();
    }

}
