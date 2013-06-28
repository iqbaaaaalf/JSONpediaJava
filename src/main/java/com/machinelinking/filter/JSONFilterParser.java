package com.machinelinking.filter;

/**
 * Parser to convert filter string syntax to {@link JSONFilter} objcts.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONFilterParser {

    void parse(String exp, JSONFilter filter);

}
