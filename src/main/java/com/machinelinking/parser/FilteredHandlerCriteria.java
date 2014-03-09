package com.machinelinking.parser;

/**
 * Defines a criteria for {@link com.machinelinking.parser.WikiTextParserFilteredHandler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface FilteredHandlerCriteria {

    /**
     * Defines whether  or not an event must be filtered on the basis of the current section and nesting levels.
     *
     * @param paragraphIndex
     * @param sectionLevel
     * @param nestingLevel
     * @return <code>true</true> if the event must be filtered, <code>false</code> otherwise.
     */
    boolean mustFilter(int paragraphIndex, int sectionLevel, int nestingLevel);

}
