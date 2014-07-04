package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.Document;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

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
                (Integer) in.get(ID_FIELD),
                (Integer) in.get(VERSION_FIELD),
                (String) in.get(NAME_FIELD),
                (DBObject) in.get(CONTENT_FIELD)
        );
    }

    public MongoDocument(int id, Integer version, String name, DBObject content) {
        if(name == null) throw new NullPointerException("name cannot be null.");
        this.name = name;
        this.id = id;
        this.version = version;

        this.document = new BasicDBObject();
        this.document.put(ID_FIELD, id);
        this.document.put(VERSION_FIELD, version);
        this.document.put(NAME_FIELD, name);
        this.document.put(CONTENT_FIELD, content);
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

    @Override
    public String toJSON() {
        return JSON.serialize(getContent());
    }

}
