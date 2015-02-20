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

    private final DBObject dbObject;

    public static MongoDocument unwrap(DBObject in) {
        if(in == null) return null;
        if(
            in.containsField(ID_FIELD) &&
            in.containsField(VERSION_FIELD) &&
            in.containsField(NAME_FIELD) &&
            in.containsField(CONTENT_FIELD)
        ) return new MongoDocument(in);
        else throw new IllegalArgumentException();
    }

    public MongoDocument(int id, Integer version, String name, DBObject content) {
        this.dbObject = new BasicDBObject();
        this.dbObject.put(ID_FIELD, id);
        if(version != null) this.dbObject.put(VERSION_FIELD, version);
        if(name != null) this.dbObject.put(NAME_FIELD, name);
        if(content != null) this.dbObject.put(CONTENT_FIELD, content);
    }

    private MongoDocument(DBObject content) {
        this.dbObject = content;
    }

    public DBObject getInternal() {
        return this.dbObject;
    }

    @Override
    public int getId() {
        return (int) dbObject.get(ID_FIELD);
    }

    @Override
    public int getVersion() {
        return (int) dbObject.get(VERSION_FIELD);
    }

    @Override
    public String getName() {
        return (String) dbObject.get(NAME_FIELD);
    }

    @Override
    public DBObject getContent() {
        return (DBObject) dbObject.get(CONTENT_FIELD);
    }

    @Override
    public String toJSON() {
        return JSON.serialize(getContent());
    }

}
