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

package com.machinelinking.freebase;

import org.codehaus.jackson.JsonNode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FreebaseServiceTest {

    @Test
    public void testEnrichment() throws IOException {
        final FreebaseService service = FreebaseService.getInstance();
        final JsonNode node = service.getEntityData("Albert Einstein");
        org.testng.Assert.assertNotNull(node);
        Assert.assertTrue(node.has("mid"));
        Assert.assertTrue(node.has("id"));
        Assert.assertTrue(node.has("name"));
    }

}
