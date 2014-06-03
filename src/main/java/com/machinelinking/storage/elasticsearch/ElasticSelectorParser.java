package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.Criteria;
import com.machinelinking.storage.SelectorParser;
import com.machinelinking.storage.SelectorParserException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticSelectorParser implements SelectorParser {

    public static final String CRITERIA_SEPARATOR = "\\s+";
    public static final String FIELD_VALUE_SEPARATOR = ":";

    private static ElasticSelectorParser instance;

    public static ElasticSelectorParser getInstance() {
        if(instance == null)
            instance = new ElasticSelectorParser();
        return instance;
    }

    private ElasticSelectorParser() {}

    @Override
    public ElasticSelector parse(String qry) throws SelectorParserException {
        final ElasticSelector selector = new ElasticSelector();
        final String[] parts = qry.split(CRITERIA_SEPARATOR);
        for(String part:parts) {
            final String[] kv = part.split(FIELD_VALUE_SEPARATOR);
            if(kv.length == 1) {
                selector.addCriteria(new Criteria(null, Criteria.Operator.eq, kv[0]));
            } else if(kv.length == 2) {
                selector.addCriteria(new Criteria(kv[0], Criteria.Operator.eq, kv[1]));
            } else {
                throw new SelectorParserException("Invalid key/value criteria " + part);
            }
        }
        return selector;
    }
}
