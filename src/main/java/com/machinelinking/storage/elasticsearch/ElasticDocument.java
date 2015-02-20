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

    private final Map<String,Object> document;

    public static ElasticDocument unwrap(Map<String,Object> in) {
        if(in == null) return null;
        if(
            in.containsKey(ID_FIELD) &&
            in.containsKey(VERSION_FIELD) &&
            in.containsKey(NAME_FIELD) &&
            in.containsKey(CONTENT_FIELD)
        ) return new ElasticDocument(in);
        else throw new IllegalArgumentException();
    }

    public ElasticDocument(int id, Integer version, String name, Map<String,?> content) {
        this.document = new HashMap<>();
        this.document.put(ID_FIELD, id);
        if(version != null) this.document.put(VERSION_FIELD, version);
        if(name != null) this.document.put(NAME_FIELD, name);
        if(content != null) this.document.put(CONTENT_FIELD, content);
    }

    private ElasticDocument(Map<String,Object> in) {
        this.document = in;
    }

    public Map<String,Object> getInternal() {
        return document;
    }

    @Override
    public int getId() {
        return (int) document.get(ID_FIELD);
    }

    @Override
    public int getVersion() {
        return (int) document.get(VERSION_FIELD);
    }

    @Override
    public String getName() {
        return (String) document.get(NAME_FIELD);
    }

    @Override
    public Map<String,Object> getContent() {
        return (Map<String,Object>) document.get(CONTENT_FIELD);
    }

    @Override
    public String toJSON() {
        return JSONUtils.toHumanReadable(document);
    }

}
