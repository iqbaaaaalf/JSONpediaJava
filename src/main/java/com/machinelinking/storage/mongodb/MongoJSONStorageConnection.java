package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.Criteria;
import com.machinelinking.storage.DocumentConverter;
import com.machinelinking.storage.JSONStorageConnection;
import com.machinelinking.storage.JSONStorageConnectionException;
import com.machinelinking.wikimedia.WikiPage;
import com.mongodb.BasicDBObject;
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
public class MongoJSONStorageConnection implements JSONStorageConnection<MongoDocument, MongoSelector> {

    public static final int BUFFER_FLUSH_SIZE = 1024;

    private final DBCollection collection;

    private final DocumentConverter<MongoDocument> converter;

    private final List<DBObject> buffer = new ArrayList<>();

    protected static DBObject selectorToDBObjectSelector(MongoSelector selector) {
        final DBObject obj = new BasicDBObject();
        for(Criteria criteria : selector.getCriterias()) {
            obj.put(criteria.field, toValue(criteria.operator, criteria.value));
        }
        return obj;
    }

    protected static DBObject selectorToDBObjectProjector(MongoSelector selector) {
        final DBObject obj = new BasicDBObject();
        for(Criteria criteria : selector.getCriterias()) {
            obj.put(criteria.field, 1);
        }
        for(String projection : selector.getProjections()) {
            obj.put(projection, 1);
        }
        return obj;
    }

    private static Object toValue(Criteria.Operator operator, Object value) {
        switch (operator) {
            case eq:
                return value;
            case neq:
                return new BasicDBObject("$neq", value);
            case gt:
                return new BasicDBObject("$gt", value);
            case gte:
                return new BasicDBObject("$gte", value);
            case lt:
                return new BasicDBObject("$lt", value);
            case lte:
                return new BasicDBObject("$lte", value);
            default:
                throw new UnsupportedOperationException();
        }
    }

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
    public MongoResultSet query(MongoSelector selector, int limit) throws JSONStorageConnectionException {
        try {
            return new MongoResultSet(
                    collection.find(
                            selectorToDBObjectSelector(selector),
                            selectorToDBObjectProjector(selector)
                    ).limit(limit)
            );
        } catch (Exception e) {
            throw new JSONStorageConnectionException("Error while performing query.", e);
        }
    }

    @Override
    public void flush() {
        flushBuffer(true);
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
