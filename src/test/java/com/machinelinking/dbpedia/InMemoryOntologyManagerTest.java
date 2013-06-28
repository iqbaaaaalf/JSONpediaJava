package com.machinelinking.dbpedia;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InMemoryOntologyManagerTest {

    @Test
    public void testInitialization() throws OntologyManagerException {
        Map<String,Property> propertyMappings = InMemoryOntologyManager.initOntologyIndex(false);
        Assert.assertTrue(propertyMappings.size() > 24700);
        for(Map.Entry<String,Property> mapping : propertyMappings.entrySet()) {
            Assert.assertNotNull(mapping.getValue().getPropertyName());
            Assert.assertNotNull(mapping.getValue().getPropertyLabel());
        }
    }

    @Test
    public void testGetMapping() throws OntologyManagerException {
        final InMemoryOntologyManager manager = new InMemoryOntologyManager();
        Assert.assertEquals(
                "{property=birthDate label='birth date' domain=Person range=xsd:date}",
                manager.getProperty("birthDate").toString()
        );
        Assert.assertEquals(
                "{property=birthPlace label='birth place' domain=Person range=Place}",
                manager.getProperty("birthPlace").toString()
        );
        Assert.assertEquals(
                "{property=spouse label='spouse' domain=Person range=Person}",
                manager.getProperty("spouse").toString()
        );
        Assert.assertEquals(
                "{property=successor label='successor' domain=null range=null}",
                manager.getProperty("successor").toString()
        );
    }

}
