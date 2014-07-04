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
                (Integer) in.get(ID_FIELD),
                (Integer) in.get(VERSION_FIELD),
                (String) in.get(NAME_FIELD),
                (Map<String,?>) in.get(CONTENT_FIELD)
        );
    }

    public ElasticDocument(int id, int version, String name, Map<String,?> document) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.document = new HashMap<>();
        this.document.put(ID_FIELD, id);
        this.document.put(VERSION_FIELD, version);
        this.document.put(NAME_FIELD, name);
        this.document.put(CONTENT_FIELD, document);
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
