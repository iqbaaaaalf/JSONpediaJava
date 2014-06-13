package com.machinelinking.storage;

import com.machinelinking.wikimedia.WikiPage;
import org.codehaus.jackson.util.TokenBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MultiJSONStorageConnection implements JSONStorageConnection<MultiDocument,MultiSelector> {

    private final JSONStorageConnection[] connections;

    MultiJSONStorageConnection(JSONStorageConnection[] connections) {
        this.connections = connections;
    }

    @Override
    public MultiDocument createDocument(WikiPage page, TokenBuffer buffer) throws JSONStorageConnectionException {
        final List<Document> documents = new ArrayList<>();
        for(JSONStorageConnection connection : connections) {
            documents.add(connection.createDocument(page, buffer));
        }
        return new MultiDocument(
                connections,
                documents.toArray( new Document[documents.size()] )
        );
    }

    @Override
    public void addDocument(MultiDocument document) throws JSONStorageConnectionException {
        for(JSONStorageConnection connection : connections) {
            connection.addDocument(document.getDocumentForConnection(connection));
        }
    }

    @Override
    public void removeDocument(int id) throws JSONStorageConnectionException {
        for (JSONStorageConnection connection : connections) {
            connection.removeDocument(id);
        }
    }

    @Override
    public MultiDocument getDocument(int id) throws JSONStorageConnectionException {
        final List<Document> documents = new ArrayList<>();
        for (JSONStorageConnection connection : connections) {
            documents.add(connection.getDocument(id));
        }
        return new MultiDocument(
                connections,
                documents.toArray(new Document[documents.size()])
        );
    }

    @Override
    public long getDocumentsCount() throws JSONStorageConnectionException {
        float total = 0;
        for (JSONStorageConnection connection : connections) {
            total += connection.getDocumentsCount();
        }
        return (long) (total / connections.length);
    }

    @Override
    public ResultSet<MultiDocument> query(MultiSelector selector, int limit) throws JSONStorageConnectionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        for (JSONStorageConnection connection : connections) {
            connection.flush();
        }
    }

    @Override
    public void close() {
        for (JSONStorageConnection connection : connections) {
            connection.close();
        }
    }
}