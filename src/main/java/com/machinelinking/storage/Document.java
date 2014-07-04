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
