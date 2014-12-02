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

package com.machinelinking.extractor;

import com.machinelinking.parser.FilteredHandlerCriteria;

/**
 * Defines a {@link FilteredHandlerCriteria to extract abstracts}.
 *
 * @author Michele Mostarda (me@michelemostarda.it)
 */
public class AbstractFilteredHandlerCriteria implements FilteredHandlerCriteria {

    public static final FilteredHandlerCriteria INSTANCE = new AbstractFilteredHandlerCriteria();
    public static final FilteredHandlerCriteria NOT_ABSTRACT_INSTANCE = new FilteredHandlerCriteria() {
        @Override
        public boolean mustFilter(int paragraphIndex, int sectionLevel, int nestingLevel, boolean plainTextFound) {
            return !INSTANCE.mustFilter(paragraphIndex, sectionLevel, nestingLevel, plainTextFound);
        }
    };

    @Override
    public boolean mustFilter(
            int paragraphIndex, int sectionLevel, int nestingLevel, boolean plainTextFound
    ) {
        return !plainTextFound || sectionLevel != -1;
    }

}
