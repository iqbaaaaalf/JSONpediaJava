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

}
