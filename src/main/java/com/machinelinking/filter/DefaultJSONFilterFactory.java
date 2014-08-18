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

/**
 * Default implementation of {@link com.machinelinking.filter.JSONFilterFactory}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONFilterFactory implements JSONFilterFactory {

    public static final JSONFilter EMPTY_FILTER = new JSONFilter() {
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void setNested(JSONFilter nested) {
            throw new IllegalStateException();
        }

        @Override
        public JSONFilter getNested() {
            return null;
        }

        @Override
        public String humanReadable() {
            return "<empty>";
        }
    };

    @Override
    public JSONFilter createEmptyFilter() {
        return EMPTY_FILTER;
    }

    @Override
    public JSONObjectFilter createJSONObjectFilter() {
        return new DefaultJSONObjectFilter();
    }

    @Override
    public JSONKeyFilter createJSONJsonKeyFilter() {
        return new DefaultJSONKeyFilter();
    }

}
