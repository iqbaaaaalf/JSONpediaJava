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
