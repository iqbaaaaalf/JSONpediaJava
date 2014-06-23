package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.DocumentConverter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
class ElasticDocumentConverter implements DocumentConverter<ElasticDocument> {

    final Set<String> blocked = new HashSet<>();

    ElasticDocumentConverter() {
        blocked.add("wikitext-json");
        blocked.add("infobox-splitter");
        blocked.add("table-splitter");
    }
    @Override
    public ElasticDocument convert(ElasticDocument in) {
        final Map<String,Object> content = (Map<String,Object>) in.getContent().get("content");
        final Map<String, Object> out = new HashMap<>();
        for (Map.Entry<String, ?> entry : content.entrySet()) {
            if (blocked.contains(entry.getKey())) continue;
            out.put(entry.getKey(), entry.getValue());
        }
        return new ElasticDocument(in.getId(), in.getVersion(), in.getName(), out);
    }
}
