package com.machinelinking.storage;

/**
 * Converts a selection query string to a selector object.
 *
 * @see com.machinelinking.storage.Selector
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface SelectorParser {

    /**
     * Parses a query to a selector.
     *
     * @param qry
     * @return
     */
    Selector parse(String qry) throws SelectorParserException;

}
