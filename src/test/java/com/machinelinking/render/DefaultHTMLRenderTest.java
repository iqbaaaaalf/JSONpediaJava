package com.machinelinking.render;

import com.machinelinking.util.JSONUtils;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultHTMLRenderTest {

    private DefaultHTMLRender render;

    @Before
    public void setUp() {
        render = new DefaultHTMLRender();
        render.addNodeRender("reference", new ReferenceNodeRender());
        render.addNodeRender("link"     , new LinkNodeRender());
        render.addNodeRender("section"  , new SectionRender());
        render.addNodeRender("template" , new CiteWebNodeRender());

        render.addKeyValueRender("url"         , new URLKeyValueRender());
        render.addKeyValueRender("archiveurl"  , new URLKeyValueRender());
        render.addKeyValueRender("title"       , new TitleKeyValueRender());
    }

    @Test
    public void testRender() throws IOException {
        //final JsonNode node = JSONUtils.parseJSON( this.getClass().getResourceAsStream("/Page1.json") );
        final JsonNode node = JSONUtils.parseJSON( this.getClass().getResourceAsStream("/Enrichment.json") );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DefaultHTMLWriter writer = new DefaultHTMLWriter( new OutputStreamWriter(baos) );
        render.processRoot(node, writer);
        System.out.println(baos);
        FileUtils.writeStringToFile( new File("/Users/hardest/Desktop/test-render.html"), baos.toString());
    }

}
