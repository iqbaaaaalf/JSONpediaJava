package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.Document;
import com.machinelinking.util.JSONUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link com.machinelinking.storage.Document} implementation for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticDocument implements Document<Map<String,Object>> {

    private final int id;
    private final int version;
    private final String name;
    private final Map<String,Object> document;

    public static ElasticDocument unwrap(Map<String,?> in) {
        return new ElasticDocument(
                (Integer) in.get("_id"),
                (Integer) in.get("version"),
                (String) in.get("name"),
                (Map<String,?>) in.get("content")
        );
    }

    public ElasticDocument(int id, int version, String name, Map<String,?> document) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.document = new HashMap<>();
        this.document.put("_id", id);
        this.document.put("version", version);
        this.document.put("name", name);
        this.document.put("content", document);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String,Object> getContent() {
        return document;
    }

    @Override
    public String toJSON() {
        return JSONUtils.toHumanReadable(document);
    }

}
