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

/**
 * <i>Factory</i> for {@link OntologyManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class OntologyManagerFactory {

    private static OntologyManagerFactory instance;

    public static OntologyManagerFactory getInstance() {
        if(instance == null) {
            instance = new OntologyManagerFactory();
        }
        return instance;
    }

    private OntologyManagerFactory() {}

    /**
     * Creates a new instance.
     *
     * @return a new instance.
     * @throws OntologyManagerException
     */
    public OntologyManager createOntologyManager() throws OntologyManagerException {
        return new InMemoryOntologyManager();
    }

}
