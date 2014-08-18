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

import java.io.Closeable;

/**
 * Defines a <i>JSON</i> storage connection.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorageConnection<D extends Document, S extends Selector> extends Closeable {

    D createDocument(WikiPage page, TokenBuffer buffer) throws JSONStorageConnectionException;

    void addDocument(D document) throws JSONStorageConnectionException;

    void removeDocument(int id) throws JSONStorageConnectionException;

    D getDocument(int id) throws JSONStorageConnectionException;

    long getDocumentsCount() throws JSONStorageConnectionException;

    ResultSet<D> query(S selector, int limit) throws JSONStorageConnectionException;

    String query(String qry) throws JSONStorageConnectionException;

    void flush();

    void close();

}
