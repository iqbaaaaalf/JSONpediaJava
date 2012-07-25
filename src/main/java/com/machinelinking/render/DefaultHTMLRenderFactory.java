package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultHTMLRenderFactory implements HTMLRenderFactory {

    private static DefaultHTMLRenderFactory instance = new DefaultHTMLRenderFactory();

    public static DefaultHTMLRenderFactory getInstance() {
        return instance;
    }

    @Override
    public DefaultHTMLRender createRender() {
        final DefaultHTMLRender render = new DefaultHTMLRender();
        render.addNodeRender("reference", new ReferenceNodeRender());
        render.addNodeRender("link"     , new LinkNodeRender());
        render.addNodeRender("section"  , new SectionRender());
        render.addNodeRender("template" , new CiteWebNodeRender());
        render.addNodeRender("template" , new CitationNodeRender());
        render.addNodeRender("template" , new MainNodeRender());

        render.addKeyValueRender("url"        , new URLKeyValueRender());
        render.addKeyValueRender("archiveurl" , new URLKeyValueRender());
        render.addKeyValueRender("title"      , new TitleKeyValueRender());
        render.addKeyValueRender("content"    , new ContentKeyValueRender());
        render.addKeyValueRender("freebase"   , new FreebaseKeyValueRender());
        return render;
    }

    @Override
    public String renderToHTML(JsonNode rootNode) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DefaultHTMLWriter writer = new DefaultHTMLWriter( new OutputStreamWriter(baos) );
        final DefaultHTMLRender render = createRender();
        render.processRoot(rootNode, writer);
        return baos.toString();
    }

    private DefaultHTMLRenderFactory() {}

}
