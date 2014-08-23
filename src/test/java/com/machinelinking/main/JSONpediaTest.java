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

package com.machinelinking.main;

import com.machinelinking.dbpedia.OntologyManager;
import com.machinelinking.dbpedia.OntologyManagerException;
import com.machinelinking.dbpedia.TemplateMapping;
import com.machinelinking.dbpedia.TemplateMappingManager;
import com.machinelinking.dbpedia.TemplateMappingManagerException;
import com.machinelinking.freebase.FreebaseService;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Test case for {@link com.machinelinking.main.JSONpedia}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONpediaTest {

    @Test
    public void testGetOntologyManager() throws OntologyManagerException {
        final OntologyManager ontologyManager = JSONpedia.instance().getOntologyManager();
        Assert.assertNotNull( ontologyManager.getProperty("birthDate") );
    }

    @Test
    public void testGetTemplateMappingManager() throws TemplateMappingManagerException {
        final TemplateMappingManager enTemplateMappingManager = JSONpedia.instance().getTemplateMappingManager("en");
        Assert.assertTrue( enTemplateMappingManager.getMappingNames().length > 100 );
        final TemplateMapping aMapping = enTemplateMappingManager.getMapping(enTemplateMappingManager.getMappingNames()[0]);
        Assert.assertNotNull(aMapping);
        Assert.assertNotNull(aMapping.getMappingName());
    }

    @Test
    public void testGetFreebaseService() throws TemplateMappingManagerException, IOException {
        final FreebaseService freebaseService = JSONpedia.instance().getFreebaseService();
        final JsonNode londonData = freebaseService.getEntityData("London");
        Assert.assertNotNull(londonData);
    }

    @Test
    public void testRender() throws JSONpediaException, IOException {
        final String html = JSONpedia.instance().render(
                "en:Test",
                JSONUtils.parseJSON(
                        "{\"@type\" : \"link\", \"label\" : \"Hello!\",  \"url\" : \"http://path.to/somewhere/\"}"
                )
        );
        Assert.assertNotNull(html);
    }

    @Test
    public void testRunServer() throws JSONpediaException, IOException {
        JSONpedia.instance().startServer("localhost", 9998);
        new URL("http://localhost:9998/annotate/flags").openConnection().connect();
        JSONpedia.instance().stopServer();
    }

    @Test
    public void testProcessEntityById() throws JSONpediaException {
        final JsonNode root = JSONpedia.instance().process("en:Albert Einstein").json();
        Assert.assertEquals(12, root.size());
    }

    @Test
    public void testProcessEntityByURL() throws JSONpediaException {
        final JsonNode root = JSONpedia.instance().process("http://en.wikipedia.org/wiki/Albert_Einstein").json();
        Assert.assertEquals(12, root.size());
    }

    @Test
    public void testProcessEntityAsMap() throws JSONpediaException {
        final Map<String,?> root = JSONpedia.instance().process("en:Albert Einstein").map();
        Assert.assertEquals(12, root.size());
    }

    @Test
    public void testProcessEntityAsHTML() throws JSONpediaException {
        final String html = JSONpedia.instance().process("en:Albert Einstein").html();
        Assert.assertTrue(html.length() > 1000);
    }

    @Test
    public void testProcessWikiText() throws JSONpediaException {
        final JsonNode root = JSONpedia.instance()
                .process("en:Albert Einstein")
                .text("A really ''short'' description of Albert Einstein")
                .json();
        Assert.assertEquals(8, root.size());
    }

    @Test
    public void testProcessEntityWithFlags() throws JSONpediaException {
        final JsonNode root = JSONpedia.instance()
                .process("en:Albert Einstein").flags("Linkers,Validate,Structure").json();
        Assert.assertEquals(15, root.size());
    }

    @Test
    public void testProcessEntityWithFilter() throws JSONpediaException {
        final JsonNode root = JSONpedia.instance()
                .process("en:Albert Einstein").flags("Linkers,Validate,Structure").filter("__type:reference").json();
        Assert.assertEquals(2, root.size());
    }

}
