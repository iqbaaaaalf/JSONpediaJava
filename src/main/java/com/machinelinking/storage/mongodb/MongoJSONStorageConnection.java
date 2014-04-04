package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.DocumentConverter;
import com.machinelinking.storage.JSONStorageConnection;
import com.machinelinking.storage.JSONStorageConnectionException;
import com.machinelinking.wikimedia.WikiPage;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

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

    private final DocumentConverter<MongoDocument> converter;

    private final List<DBObject> buffer = new ArrayList<>();

    protected MongoJSONStorageConnection(DBCollection collection, DocumentConverter<MongoDocument> converter) {
        this.collection = collection;
        this.converter = converter;
    }

    @Override
    public MongoDocument createDocument(WikiPage page, String json) throws JSONStorageConnectionException {
        final DBObject dbNode = (DBObject) JSON.parse(json);
        return new MongoDocument(page.getId(), page.getRevId(), page.getTitle(), dbNode);
    }

    @Override
    public void addDocument(MongoDocument in) {
        final MongoDocument document = converter == null ? in : converter.convert(in);
        buffer.add(document.getContent());
        flushBuffer(false);
    }

    @Override
    public void removeDocument(int id) {
        collection.remove( new MongoDocument(id, null, null, null).getContent() );
    }

    @Override
    public MongoDocument getDocument(int id) {
        final DBObject found = collection.findOne( new MongoDocument(id, null, null, null).getContent() );
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
