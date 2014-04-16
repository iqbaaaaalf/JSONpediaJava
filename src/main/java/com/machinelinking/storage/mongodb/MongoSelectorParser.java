package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.Criteria;
import com.machinelinking.storage.SelectorParser;

import java.util.Arrays;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoSelectorParser implements SelectorParser {

    public static final String PROJECTION_OP = "->";
    public static final String SELECTOR_SEP = ",";
    public static final String PROJECTION_SEP = ",";

    public static final String SELECTOR_OP_NEQ = "!=";
    public static final String SELECTOR_OP_LTE = "<=";
    public static final String SELECTOR_OP_GTE = ">=";
    public static final String SELECTOR_OP_EQ = "=";
    public static final String SELECTOR_OP_LT = "<";
    public static final String SELECTOR_OP_GT = ">";
    public static final String[] SELECTOR_OPS = new String[]{
            SELECTOR_OP_NEQ,
            SELECTOR_OP_LTE,
            SELECTOR_OP_GTE,
            SELECTOR_OP_EQ,
            SELECTOR_OP_LT,
            SELECTOR_OP_GT,
    };

    private static MongoSelectorParser instance;

    public static MongoSelectorParser getInstance() {
        if(instance == null)
            instance = new MongoSelectorParser();
        return instance;
    }

    public static final Criteria.Operator operatorStrToOperator(String op) {
        switch (op) {
            case SELECTOR_OP_EQ:
                return Criteria.Operator.eq;
            case SELECTOR_OP_NEQ:
                return Criteria.Operator.neq;
            case SELECTOR_OP_LT:
                return Criteria.Operator.lt;
            case SELECTOR_OP_LTE:
                return Criteria.Operator.lte;
            case SELECTOR_OP_GT:
                return Criteria.Operator.gt;
            case SELECTOR_OP_GTE:
                return Criteria.Operator.gte;
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }

    private MongoSelectorParser() {}

    @Override
    public MongoSelector parse(String qry) {
        final String[] parts = qry.split(PROJECTION_OP);
        if(parts.length != 2)
            throw new IllegalArgumentException(String.format("Expected '<selectors> %s <projection>'", PROJECTION_OP));
        final String selectors = parts[0];
        final String projections = parts[1];
        final MongoSelector mongoSelector = new MongoSelector();
        addCriterias(selectors, mongoSelector);
        addProjections(projections, mongoSelector);
        return mongoSelector;
    }

    private void addCriterias(String selectorsStr, MongoSelector mongoSelector) {
        final String[] selectors = selectorsStr.split(SELECTOR_SEP);
        for(String selector : selectors) {
            selector = selector.trim();
            final int[] operator = findOperator(SELECTOR_OPS, selector);
            if(operator == null)
                throw new IllegalArgumentException(
                        String.format(
                                "A valid operator (%s) must be specified within selector '%s'",
                                Arrays.toString(SELECTOR_OPS), selector)
                );
            final String field = selector.substring(0, operator[0]).trim();
            final String operatorStr = SELECTOR_OPS[operator[1]];
            final String value = selector.substring(operator[0] + operatorStr.length()).trim();
            mongoSelector.addCriteria( new Criteria(field, operatorStrToOperator(operatorStr), value));
        }
    }

    private void addProjections(String projectionsStr, MongoSelector mongoSelector) {
        final String[] projections = projectionsStr.split(PROJECTION_SEP);
        for(String projection : projections) {
            projection = projection.trim();
            if(projection.length() == 0)
                throw new IllegalArgumentException(String.format("Invalid projection in '%s'", projectionsStr));
            mongoSelector.addProjection(projection);
        }
    }

    private int[] findOperator(String[] ops, String in) {
        int index;
        for (int i = 0; i < ops.length; i++) {
            index = in.indexOf(ops[i]);
            if(index != -1) return new int[] {index, i};
        }
        return null;
    }

}
