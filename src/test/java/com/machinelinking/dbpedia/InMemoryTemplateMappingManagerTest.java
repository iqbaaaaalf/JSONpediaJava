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

/**
 * Test case for {@link com.machinelinking.dbpedia.InMemoryTemplateMappingManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InMemoryTemplateMappingManagerTest {

    @Test
    public void testLoadMappingFromAPI() throws TemplateMappingManagerException {
        checkMappingManager(TemplateMappingFactory.getInstance().getTemplateMappingManager("en"), 400);
        checkMappingManager(TemplateMappingFactory.getInstance().getTemplateMappingManager("de"), 280);
        checkMappingManager(TemplateMappingFactory.getInstance().getTemplateMappingManager("fr"), 180);
        checkMappingManager(TemplateMappingFactory.getInstance().getTemplateMappingManager("es"), 130);
    }

    private void checkMappingManager(TemplateMappingManager manager, int expectedMappings) {
        Assert.assertTrue(manager.getMappingsCount() > expectedMappings);
        Assert.assertEquals(manager.getMappingNames().length, manager.getMappingsCount());
        for (String name : manager.getMappingNames()) {
            Assert.assertNotNull(manager.getMapping(name));
        }
    }

}
