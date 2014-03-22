package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.JSONStorageConnection;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link com.machinelinking.storage.JSONStorageConnection} for <i>MongoDB</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageConnection implements JSONStorageConnection<MongoDocument> {

    public static final int BUFFER_FLUSH_SIZE = 1024;

    private final DBCollection collection;

    private final List<DBObject> buffer = new ArrayList<>();

    protected MongoJSONStorageConnection(DBCollection collection) {
        this.collection = collection;
    }

    @Override
    public void addDocument(MongoDocument document) {
        buffer.add(document.getContent());
        flushBuffer(false);
    }

    @Override
    public void removeDocument(String docId) {
        collection.remove( new MongoDocument(docId, null, null, null).getContent() );
    }

    @Override
    public MongoDocument getDocument(String docId) {
        final DBObject found = collection.findOne( new MongoDocument(docId, null, null, null).getContent()  );
        return MongoDocument.unwrap(found);
    }

    @Override
    public long getDocumentsCount() {
        return collection.count();
    }

    @Override
    public void close() {
        flushBuffer(true);
    }

    private void flushBuffer(boolean force) {
        if(force || buffer.size() > BUFFER_FLUSH_SIZE) {
            this.collection.insert(buffer);
            buffer.clear();
        }
    }

}
