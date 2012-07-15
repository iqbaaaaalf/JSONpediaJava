package com.machinelinking.storage;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DBObjectDocument implements Document<DBObject> {

    private final String id;
    private final DBObject document;

    public static DBObjectDocument unwrap(DBObject in) {
        return new DBObjectDocument( (String) in.get("_id"), (DBObject) in.get("content") );
    }

    public DBObjectDocument(String id, DBObject document) {
        if(id == null) throw new NullPointerException("id cannot be null.");
        this.id = id;

        this.document = new BasicDBObject();
        this.document.put("_id", id);
        this.document.put("content", document);
    }

    public DBObjectDocument(String id) {
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
