package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateNameExtractor extends Extractor {

    private Map<String,Integer> templateNames = new HashMap<>();

    public TemplateNameExtractor() {
        super("template-names");
    }

    @Override
    public void beginTemplate(String name) {
        name = name.trim();
        Integer count = templateNames.get(name);
        count = count == null ? 1 : count + 1;
        templateNames.put(name, count);
    }

    @Override
    public void flushContent(Serializer serializer) {
        serializer.openObject();
        serializer.fieldValue("__type", "template_name");
        serializer.field("occurrences");
        serializer.openObject();
        for(Map.Entry<String,Integer> entry : templateNames.entrySet()) {
            serializer.fieldValue(entry.getKey(), entry.getValue());
        }
        serializer.closeObject();
        serializer.closeObject();
    }

    @Override
    public void reset() {
        templateNames.clear();
    }

}
