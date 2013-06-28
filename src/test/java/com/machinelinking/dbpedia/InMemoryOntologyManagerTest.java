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
        Map<String,PropertyMapping> propertyMappings = InMemoryOntologyManager.initOntologyIndex(false);
        Assert.assertTrue(propertyMappings.size() > 24700);
        for(Map.Entry<String,PropertyMapping> mapping : propertyMappings.entrySet()) {
            Assert.assertNotNull(mapping.getValue().getPropertyName());
            Assert.assertNotNull(mapping.getValue().getPropertyLabel());
        }
    }

    @Test
    public void testGetMapping() throws OntologyManagerException {
        final InMemoryOntologyManager manager = new InMemoryOntologyManager();
        Assert.assertEquals(
                "{property=birthDate label='birth date' domain=Person range=xsd:date}",
                manager.getPropertyMapping("birthDate").toString()
        );
        Assert.assertEquals(
                "{property=birthPlace label='birth place' domain=Person range=Place}",
                manager.getPropertyMapping("birthPlace").toString()
        );
        Assert.assertEquals(
                "{property=spouse label='spouse' domain=Person range=Person}",
                manager.getPropertyMapping("spouse").toString()
        );
        Assert.assertEquals(
                "{property=successor label='successor' domain=null range=null}",
                manager.getPropertyMapping("successor").toString()
        );
    }

}
