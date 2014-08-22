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

import java.util.Set;

/**
 * Defines a manager for ontologies.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface OntologyManager {

    /**
     * Returns the number of stored properties.
     *
     * @return number of properties.
     */
    int getPropertiesCount();

    /**
     * Returns the set of property names.
     *
     * @return not <code>null</code> set of names.
     */
    Set<String> getPropertyNames();

    /**
     * Returns a {@link Property} for a given <i>property</i> name.
     *
     * @param property
     * @return a property mapping or <code>null</code> if not found.
     */
    Property getProperty(String property);

}
