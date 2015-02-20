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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test case for {@link InMemoryOntologyManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InMemoryOntologyManagerTest {

    private static final int ESPECTED_PROPS_COUNT = 3400;

    @Test
    public void testInitialization() throws OntologyManagerException {
        Map<String,Property> propertyMappings = InMemoryOntologyManager.initOntologyIndex(false);
        Assert.assertTrue(propertyMappings.size() > ESPECTED_PROPS_COUNT);
        for(Map.Entry<String,Property> mapping : propertyMappings.entrySet()) {
            Assert.assertNotNull(mapping.getValue().getPropertyName());
            Assert.assertNotNull(mapping.getValue().getPropertyLabel());
        }
    }

    @Test
    public void testGetPropertiesCount() throws OntologyManagerException {
        final InMemoryOntologyManager manager = new InMemoryOntologyManager();
        Assert.assertTrue(manager.getPropertiesCount() > ESPECTED_PROPS_COUNT);
    }

    @Test
    public void testGetPropertyNames() throws OntologyManagerException {
        final InMemoryOntologyManager manager = new InMemoryOntologyManager();
        Assert.assertEquals(manager.getPropertiesCount(), manager.getPropertyNames().size());
    }

    @Test
    public void testGetMapping() throws OntologyManagerException {
        final InMemoryOntologyManager manager = new InMemoryOntologyManager();
        Assert.assertEquals(
                manager.getProperty("birthDate").toString(),
                "{property=birthDate label='birth date' domain=Person range=xsd:date}"
        );
        Assert.assertEquals(
                manager.getProperty("birthPlace").toString(),
                "{property=birthPlace label='birth place' domain=Person range=Place}"
        );
        Assert.assertEquals(
                manager.getProperty("spouse").toString(),
                "{property=spouse label='spouse' domain=Person range=Person}"
        );
        Assert.assertEquals(
                manager.getProperty("successor").toString(),
                "{property=successor label='successor' domain=null range=null}"
        );
    }

}
