package com.machinelinking.filter;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONFilterParser implements JSONFilterParser {

    public static final String CONSTRAINT_SEPARATOR = ",";
    public static final String FIELD_SEPARATOR      = ":";

    @Override
    public void parse(String exp, JSONFilter filter) {
        if(exp == null || exp.trim().length() == 0) return;
        final String[] criterias = exp.split(CONSTRAINT_SEPARATOR);
        for(String criteria : criterias) {
            final String[] keyVal = criteria.split(FIELD_SEPARATOR);
            if(keyVal.length != 2) throw new IllegalArgumentException(
                    String.format("Invalid constraint [%s]", criteria)
            );
            filter.addCriteria(keyVal[0], keyVal[1]);
        }
    }
}
