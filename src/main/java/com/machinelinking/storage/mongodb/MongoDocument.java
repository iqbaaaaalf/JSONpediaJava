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

    public static final String ID_FIELD = "_id";
    public static final String VERSION_FIELD = "version";
    public static final String NAME_FIELD = "name";
    public static final String CONTENT_FIELD = "content";
    public static final String[] FIELDS = new String[]{ID_FIELD, VERSION_FIELD, NAME_FIELD, CONTENT_FIELD};

    private final int id;
    private final int version;
    private final String name;
    private final DBObject document;

    public static MongoDocument unwrap(DBObject in) {
        return new MongoDocument(
                (Integer) in.get("_id"),
                (Integer) in.get("version"),
                (String) in.get("name"),
                (DBObject) in.get("content")
        );
    }

    public MongoDocument(int id, Integer version, String name, DBObject content) {
        if(name == null) throw new NullPointerException("name cannot be null.");
        this.name = name;
        this.id = id;
        this.version = version;

        this.document = new BasicDBObject();
        this.document.put("_id", id);
        this.document.put("version", version);
        this.document.put("name", name);
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

    @Override
    public String toJSON() {
        return JSON.serialize(getContent());
    }

}
