package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.JSONStorageConnection;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Implementation of {@link com.machinelinking.storage.JSONStorageConnection} for <i>MongoDB</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageConnection implements JSONStorageConnection<MongoDocument> {

    private final DBCollection collection;

    protected MongoJSONStorageConnection(DBCollection collection) {
        this.collection = collection;
    }

    @Override
    public void addDocument(MongoDocument document) {
        collection.insert( document.getContent() );
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

}
