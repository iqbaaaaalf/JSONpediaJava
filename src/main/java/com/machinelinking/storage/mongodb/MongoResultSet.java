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


import com.machinelinking.storage.ResultSet;
import com.mongodb.DBCursor;

import java.io.Closeable;

/**
 * {@link com.machinelinking.storage.ResultSet} implementation for <i>MongoDB</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoResultSet implements ResultSet<MongoDocument>, Closeable {

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
