package com.machinelinking.freebase;

import com.machinelinking.freebase.FreebaseService;
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
        System.out.println("Freebase Node: " + node);
        Assert.assertNotNull(node);
    }

}
