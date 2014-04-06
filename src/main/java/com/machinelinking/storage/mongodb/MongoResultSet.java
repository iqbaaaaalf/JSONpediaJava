package com.machinelinking.storage.mongodb;


import com.machinelinking.storage.ResultSet;
import com.mongodb.DBCursor;

/**
 * {@link com.machinelinking.storage.ResultSet} implementation for <i>MongoDB</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoResultSet implements ResultSet<MongoDocument> {

    private final DBCursor cursor;

    MongoResultSet(DBCursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public long getCount() {
        return cursor.count();
    }

    @Override
    public MongoDocument next() {
        return cursor.hasNext() ? MongoDocument.unwrap(cursor.next()) : null;
    }

    @Override
    public void close() {
        cursor.close();
    }
}
