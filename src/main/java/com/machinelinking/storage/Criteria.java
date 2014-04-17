package com.machinelinking.storage;

/**
 * Defines a {@link com.machinelinking.storage.Selector} criteria.
 *
 * @see com.machinelinking.storage.Selector
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Criteria {

    public enum Operator{
        eq,
        neq,
        gt,
        gte,
        lt,
        lte
    }

    public final String field;
    public final Operator operator;
    public final Object value;

    public Criteria(String field, Operator operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", field, operator, toValue(value));
    }

    private String toValue(Object value) {
        return value instanceof Integer ? value.toString() : String.format("'%s'", value.toString());
    }

}
