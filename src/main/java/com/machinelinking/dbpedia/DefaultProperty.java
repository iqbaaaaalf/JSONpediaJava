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

package com.machinelinking.dbpedia;


import java.io.Serializable;

/**
 * Default implementation for {@link Property}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultProperty implements Property, Serializable {

    private final String property;

    private final String label;

    private final String domain;

    private final String range;

    public DefaultProperty(String property, String label, String domain, String range) {
        if(property == null) throw new NullPointerException();
        this.property = property;
        this.label = label;
        this.domain = domain;
        this.range = range;
    }

    @Override
    public String getPropertyName() {
        return property;
    }

    @Override
    public String getPropertyLabel() {
        return label;
    }

    @Override
    public String getPropertyDomain() {
        return domain;
    }

    @Override
    public String getPropertyRange() {
        return range;
    }

    @Override
    public String toString() {
        return String.format("{property=%s label='%s' domain=%s range=%s}", property, label, domain, range);
    }
}
