package com.machinelinking.filter;

/**
 * Defines a filter applicable over a <i>JSON</i> object.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONFilter {

    /**
     * @return <code>true</code> if filter is empty, <code>false</code> otherwise.
     */
    boolean isEmpty();

    /**
     * Sets the nested filter if any.
     *
     * @param nested
     */
    void setNested(JSONFilter nested);

    /**
     *
     * @return the nested filter if any or <code>null</code>.
     */
    JSONFilter getNested();

    /**
     * @return human readable version of filter.
     */
    String humanReadable();

}
