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
