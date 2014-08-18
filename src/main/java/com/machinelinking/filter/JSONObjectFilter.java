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

package com.machinelinking.filter;

import org.codehaus.jackson.JsonNode;

/**
 * Defines a filter of an object satisfying a set of criteria.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONObjectFilter extends JSONFilter {

    /**
     * Adds a filtering criteria based on exact matching.
     *
     * @param fieldName name of the field to match.
     * @param fieldPattern the pattern to match for the field value.
     */
    void addCriteria(String fieldName, String fieldPattern);

    /**
     * Returns the field pattern set for a field name.
     *
     * @param fieldName name of a field.
     * @return a regex.
     */
    String getCriteriaPattern(String fieldName);

    /**
     * @param node
     * @return <code>true</code> if match is satisfied, <code>false</code> otherwise.
     */
    boolean match(JsonNode node);
}
