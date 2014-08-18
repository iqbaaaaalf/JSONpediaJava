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
 * Defines a filter applicable over a <i>JSON</i> object.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONFilter {

    /**
     * @return <code>true</code> if filter is empty, <code>false</code> otherwise.
     */
    boolean isEmpty();

    /**
     * Sets the nested filter if any.
     *
     * @param nested
     */
    void setNested(JSONFilter nested);

    /**
     *
     * @return the nested filter if any or <code>null</code>.
     */
    JSONFilter getNested();

    /**
     * @return human readable version of filter.
     */
    String humanReadable();

}
