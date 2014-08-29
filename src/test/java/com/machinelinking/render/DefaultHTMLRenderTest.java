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

package com.machinelinking.render;

import com.machinelinking.template.RenderScope;
import com.machinelinking.util.JSONUtils;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Test case for {@link DefaultHTMLRender}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultHTMLRenderTest {

    @Test
    public void testRender() throws IOException, NodeRenderException {
        final JsonNode node = JSONUtils.parseJSON(
                this.getClass().getResourceAsStream(
                        "Enrichment1.json"
                )
        );
        final URL documentURL = new URL("http://en.wikipedia.org/page/Fake");
        final DocumentContext context = new DefaultDocumentContext(RenderScope.FULL_RENDERING, documentURL);
        final String html = DefaultHTMLRenderFactory.getInstance().createRender().renderDocument(context, node);
        FileUtils.writeStringToFile( new File("./test-render.html"), html);
    }

}
