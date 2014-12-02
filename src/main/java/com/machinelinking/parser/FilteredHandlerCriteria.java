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

package com.machinelinking.parser;

/**
 * Defines a criteria for {@link com.machinelinking.parser.WikiTextParserFilteredHandler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface FilteredHandlerCriteria {

    /**
     * Defines whether  or not an event must be filtered on the basis of the current section and nesting levels.
     *
     * @param paragraphIndex index of the current paragraph (paragraphs are separated by <code>\n\n+</code>).
     * @param sectionLevel index of current section. Until first section index is <code>-1</code>.
     * @param nestingLevel index of event nesting inside other events.
     * @param plainTextFound <code>true</code> if plain text at nesting level <code>0</code> has been found.
     * @return <code>true</true> if the event must be filtered, <code>false</code> otherwise.
     */
    boolean mustFilter(int paragraphIndex, int sectionLevel, int nestingLevel, boolean plainTextFound);

}
