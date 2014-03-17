package com.machinelinking.filter;

import com.machinelinking.parser.Attribute;
import com.machinelinking.parser.AttributeScanner;

/**
 * Default {@link JSONFilterParser} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONFilterParser implements JSONFilterParser {

    public static final char NESTING_SEPARATOR     = '>';
    public static final char CONSTRAINT_SEPARATOR  = ',';
    public static final char FIELD_SEPARATOR       = ':';
    public static final char FIELD_VALUE_DELIMITER = '"';

    @Override
    public JSONFilter parse(String exp, JSONFilterFactory factory) {
        if(exp == null || exp.trim().length() == 0) return factory.createEmptyFilter();

        final String[] levels = exp.split(Character.toString(NESTING_SEPARATOR));
        JSONFilter current;
        JSONFilter top = null;
        JSONFilter prev = null;
        for(String level : levels) {
            if(level.trim().length() == 0)
                throw new IllegalArgumentException(String.format("Invalid selector content [%s].", level));
            if(!level.contains(Character.toString(FIELD_SEPARATOR))) {
                JSONKeyFilter keyFilter = factory.createJSONJsonKeyFilter();
                parseKeyFilter(level, keyFilter);
                current = keyFilter;
            } else {
                JSONObjectFilter objectFilter = factory.createJSONObjectFilter();
                parseObjectFilter(level, objectFilter);
                current = objectFilter;
            }
            if(top == null) top = current;
            if(prev != null) prev.setNested(current);
            prev = current;
        }
        return top;
    }

    private void parseKeyFilter(String level, JSONKeyFilter newFilter) {
        newFilter.setCriteria(level);
    }

    private void parseObjectFilter(String level, JSONObjectFilter newFilter) {
        final Attribute[] attributes = AttributeScanner.scan(
                CONSTRAINT_SEPARATOR, FIELD_SEPARATOR, FIELD_VALUE_DELIMITER, level
        );
        for(Attribute attribute : attributes) {
            newFilter.addCriteria(attribute.name, attribute.value);
        }
    }
}
