package com.machinelinking.filter;

/**
 * Define a filter for the value associated with a JSON object key.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONKeyFilter extends JSONFilter {

    public void setCriteria(String criteria);

    boolean matchKey(String key);

}
