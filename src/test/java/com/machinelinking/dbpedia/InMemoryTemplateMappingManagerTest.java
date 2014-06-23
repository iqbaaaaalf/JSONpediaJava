package com.machinelinking.dbpedia;

import junit.framework.Assert;
import org.junit.Test;

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
        Assert.assertEquals(manager.getMappingsCount(), manager.getMappingNames().length);
        for (String name : manager.getMappingNames()) {
            Assert.assertNotNull(manager.getMapping(name));
        }
    }

}
