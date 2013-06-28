package com.machinelinking.dbpedia;

/**
 * Defines a manager for ontologies.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface OntologyManager {

    /**
     * Returns a {@link Property} for a given <i>property</i> name.
     *
     * @param property
     * @return a property mapping or <code>null</code> if not found.
     */
    Property getProperty(String property);

}
