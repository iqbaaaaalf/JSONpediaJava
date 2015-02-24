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

package com.machinelinking.storage.elasticsearch;

import com.machinelinking.parser.Attribute;
import com.machinelinking.parser.AttributeScanner;
import com.machinelinking.storage.Criteria;
import com.machinelinking.storage.SelectorParser;
import com.machinelinking.storage.SelectorParserException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticSelectorParser implements SelectorParser {

    public static final char CRITERIA_SEPARATOR = ' ';
    public static final char FIELD_VALUE_SEPARATOR = ':';
    public static final char CRITERIA_DELIMITER = '"';

    private static ElasticSelectorParser instance;

    public static ElasticSelectorParser getInstance() {
        if(instance == null)
            instance = new ElasticSelectorParser();
        return instance;
    }

    private ElasticSelectorParser() {}

    @Override
    public ElasticSelector parse(String qry) throws SelectorParserException {
        final Attribute[] attributes = AttributeScanner.scan(
                CRITERIA_SEPARATOR, FIELD_VALUE_SEPARATOR, CRITERIA_DELIMITER, qry
        );
        final ElasticSelector selector = new ElasticSelector();
        for(Attribute attribute : attributes) {
            selector.addCriteria(new Criteria(attribute.name, Criteria.Operator.eq, attribute.value));
        }
        return selector;
    }
}
