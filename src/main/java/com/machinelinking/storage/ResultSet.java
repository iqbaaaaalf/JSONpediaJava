package com.machinelinking.storage;

/**
 * Defines the resultset of a {@link com.machinelinking.storage.Selector}.
 *
 * @see com.machinelinking.storage.Selector
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface ResultSet<D extends Document> {

    /**
     * @return number of total results.
     */
    long getCount();

    /**
     * @return next document or <code>null</code>.
     */
    D next();

    /**
     * Closes the resultset cursor.
     */
    void close();

}
