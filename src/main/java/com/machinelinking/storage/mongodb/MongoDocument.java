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

    private final String id;
    private final DBObject document;

    public static MongoDocument unwrap(DBObject in) {
        return new MongoDocument( (String) in.get("_id"), (DBObject) in.get("content") );
    }

    public MongoDocument(String id, DBObject document) {
        if(id == null) throw new NullPointerException("id cannot be null.");
        this.id = id;

        this.document = new BasicDBObject();
        this.document.put("_id", id);
        this.document.put("content", document);
    }

    public MongoDocument(String id) {
        this(id, null);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public DBObject getDocument() {
        return document;
    }

}
