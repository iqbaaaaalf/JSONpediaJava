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
 * Default {@link com.machinelinking.filter.JSONKeyFilter} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONKeyFilter implements JSONKeyFilter {

    private String criteria;
    private JSONFilter nested;

    public DefaultJSONKeyFilter() {}

    public void setCriteria(String c) {
        if(this.criteria != null) throw new IllegalStateException();
        this.criteria = c;
    }

    public void setNested(JSONFilter n) {
        if(this.nested != null) throw new IllegalStateException();
        this.nested = n;
    }

    @Override
    public boolean matchKey(String key) {
        return criteria.matches(key);
    }

    @Override
    public JSONFilter getNested() {
        return nested;
    }

    @Override
    public boolean isEmpty() {
        return criteria == null;
    }

    @Override
    public String humanReadable() {
        return String.format("key_filter(%s)>%s", criteria, nested == null ? null : nested.humanReadable());
    }

    @Override
    public String toString() {
        return humanReadable();
    }
}
