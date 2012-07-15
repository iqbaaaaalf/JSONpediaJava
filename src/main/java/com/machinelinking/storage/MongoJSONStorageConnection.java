package com.machinelinking.storage;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageConnection implements JSONStorageConnection<DBObjectDocument> {

    private final DBCollection collection;

    protected MongoJSONStorageConnection(DBCollection collection) {
        this.collection = collection;
    }

    @Override
    public void addDocument(DBObjectDocument document) {
        collection.insert( document.getDocument() );
    }

    @Override
    public void removeDocument(String docId) {
        collection.remove( new DBObjectDocument(docId).getDocument() );
    }

    @Override
    public DBObjectDocument getDocument(String docId) {
        final DBObject found = collection.findOne( new DBObjectDocument(docId).getDocument()  );
        return DBObjectDocument.unwrap(found);
    }

    @Override
    public long getDocumentsCount() {
        return collection.count();
    }

}
