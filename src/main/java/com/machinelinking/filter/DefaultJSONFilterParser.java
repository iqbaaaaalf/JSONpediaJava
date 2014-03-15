package com.machinelinking.filter;

import com.machinelinking.parser.Attribute;
import com.machinelinking.parser.AttributeScanner;

/**
 * Default {@link JSONFilterParser} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONFilterParser implements JSONFilterParser {

    public static final char CONSTRAINT_SEPARATOR  = ',';
    public static final char FIELD_SEPARATOR       = ':';
    public static final char FIELD_VALUE_DELIMITER = '"';

    @Override
    public void parse(String exp, JSONFilter filter) {
        if(exp == null || exp.trim().length() == 0) return;

        final Attribute[] attributes = AttributeScanner.scan(
                CONSTRAINT_SEPARATOR, FIELD_SEPARATOR, FIELD_VALUE_DELIMITER, exp
        );
        for(Attribute attribute : attributes) {
            filter.addCriteria(attribute.name, attribute.value);
        }
    }
}
