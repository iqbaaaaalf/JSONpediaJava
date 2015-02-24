/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

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
        return String.format("%s %s %s", field == null ? "_any" : field, operator, toValue(value));
    }

    private String toValue(Object value) {
        return value instanceof Integer ? value.toString() : String.format("'%s'", value.toString());
    }

}
