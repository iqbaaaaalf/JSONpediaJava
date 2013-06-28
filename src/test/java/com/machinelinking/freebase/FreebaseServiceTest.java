package com.machinelinking.freebase;

import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FreebaseServiceTest {

    @Test
    public void testEnrichment() throws IOException {
        final FreebaseService service = FreebaseService.getInstance();
        final JsonNode node = service.getEntityData("Albert Einstein");
        Assert.assertNotNull(node);
        Assert.assertTrue(node.has("mid"));
        Assert.assertTrue(node.has("id"));
        Assert.assertTrue(node.has("name"));
    }

}
