package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.Document;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * {@link com.machinelinking.storage.Document} implementation for {@link DBObject}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoDocument implements Document<DBObject> {

    private final int id;
    private final int version;
    private final String name;
    private final DBObject document;

    public static MongoDocument unwrap(DBObject in) {
        return new MongoDocument(
                (String) in.get("_id"),
                (Integer) in.get("id"),
                (Integer) in.get("version"),
                (DBObject) in.get("content")
        );
    }

    public MongoDocument(String name, Integer id,  Integer version, DBObject content) {
        if(name == null) throw new NullPointerException("name cannot be null.");
        this.name = name;
        this.id = id;
        this.version = version;

        this.document = new BasicDBObject();
        this.document.put("_id", name);
        this.document.put("id", id);
        this.document.put("version", version);
        this.document.put("content", content);
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
    public DBObject getContent() {
        return document;
    }

}
