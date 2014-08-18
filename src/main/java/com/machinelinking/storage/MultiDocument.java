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

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MultiDocument implements Document {

    private final Document[] documents;
    private final JSONStorageConnection[] connections;

    protected MultiDocument(JSONStorageConnection[] connections, Document[] documents) {
        if(documents.length == 0) throw new IllegalArgumentException();
        if(documents.length != connections.length) throw new IllegalArgumentException();
        this.connections = connections;
        this.documents = documents;
    }

    public Document getDocumentForConnection(JSONStorageConnection c) {
        for(int i = 0; i < connections.length; i++) {
            if(connections[i] == c) return documents[i];
        }
        throw new IllegalArgumentException();
    }

    @Override
    public int getId() {
        return getSample().getId();
    }

    @Override
    public int getVersion() {
        return getSample().getVersion();
    }

    @Override
    public String getName() {
        return getSample().getName();
    }

    @Override
    public Object getContent() {
        return getSample().getContent();
    }

    @Override
    public String toJSON() {
        return getSample().toJSON();
    }

    private Document getSample() {
        return documents[0];
    }

}
