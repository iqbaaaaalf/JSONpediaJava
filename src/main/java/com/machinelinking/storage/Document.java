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
 * Defines a <i>Wikitext</i> document.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Document<T> {

    public static final String ID_FIELD = "_id";
    public static final String VERSION_FIELD = "version";
    public static final String NAME_FIELD = "name";
    public static final String CONTENT_FIELD = "content";
    public static final String[] FIELDS = new String[]{ID_FIELD, VERSION_FIELD, NAME_FIELD, CONTENT_FIELD};

    int getId();

    int getVersion();

    String getName();

    T getContent();

    String toJSON();

}
