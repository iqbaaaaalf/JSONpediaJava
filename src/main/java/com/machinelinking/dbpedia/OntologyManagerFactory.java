package com.machinelinking.dbpedia;

/**
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

    public OntologyManager createOntologyManager() throws OntologyManagerException {
        return new InMemoryOntologyManager();
    }

}
