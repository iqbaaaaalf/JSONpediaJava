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

import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Test case for {@link com.machinelinking.dbpedia.TemplateMappingFactory} lookup.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateMappingFactoryTest {

    private static final Logger logger = Logger.getLogger(TemplateMappingFactoryTest.class);

    @Test
    public void testToJSON() throws IOException, WikiTextParserException, SAXException {
        final TemplateMapping mapping = TemplateMappingFactory.getInstance().readMappingForTemplate("Chembox");
        final String json = JSONUtils.serializeToJSON(mapping);
        logger.debug(json);
        Assert.assertEquals(
                JSONUtils.parseJSON( this.getClass().getResourceAsStream("Mapping1.json") ),
                JSONUtils.parseJSON(json)
        );
    }

    @Test
    public void testReadMappingForTemplate() throws IOException, WikiTextParserException, SAXException {
        final TemplateMapping mapping = TemplateMappingFactory.getInstance().readMappingForTemplate("Infobox scientist");
        logger.debug(JSONUtils.serializeToJSON(mapping));
        Assert.assertEquals( 23, mapping.getMappingSize() );
    }

}
