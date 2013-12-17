package com.machinelinking.render;

/**
 * Default implementation of {@link HTMLRenderFactory}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultHTMLRenderFactory implements HTMLRenderFactory {

    private static DefaultHTMLRenderFactory instance = new DefaultHTMLRenderFactory();

    public static DefaultHTMLRenderFactory getInstance() {
        return instance;
    }

    @Override
    public DefaultHTMLRender createRender() {
        final DefaultHTMLRender render = new DefaultHTMLRender(true);
        // Root level.
        render.addKeyValueRender("freebase", new FreebaseKeyValueRender());
        render.addKeyValueRender("issues"  , new IssuesKeyValueRender());
        render.addKeyValueRender("abstract", new AbstractKeyValueRender());
        render.addKeyValueRender("sections", new SectionsKeyValueRender());
        // TODO: use link and reference sections.
        render.addKeyValueRender("links"   , new LinksKeyValueRender());
        render.addKeyValueRender("references", new ReferencesKeyValueRender());
        render.addKeyValueRender("templates", new TemplatesKeyValueRender());
        render.addKeyValueRender("categories", new CategoriesKeyValueRender());
        render.addKeyValueRender("template-mapping", new TemplatesMappingKeyValueRender());

        // Within wikitext-json.structure element.
        render.addNodeRender("reference", new ReferenceNodeRender());
        render.addNodeRender("link"     , new LinkNodeRender());
        render.addNodeRender("section"  , new SectionRender());
        render.addNodeRender("template" , new CiteWebNodeRender());
        render.addNodeRender("template" , new CitationNodeRender());
        render.addNodeRender("template" , new MainNodeRender());
        render.addNodeRender("table"    , new TableNodeRender());
        render.addKeyValueRender("url"        , new URLKeyValueRender());
        render.addKeyValueRender("archiveurl" , new URLKeyValueRender());
        render.addKeyValueRender("title"      , new TitleKeyValueRender());
        render.addKeyValueRender("content"    , new ContentKeyValueRender());

        render.addPrimitiveRender( new BaseTextPrimitiveNodeRender() );
        return render;
    }

    private DefaultHTMLRenderFactory() {}

}
