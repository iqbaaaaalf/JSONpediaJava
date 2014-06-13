package com.machinelinking.render;

import com.machinelinking.template.TemplateNodeRender;

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

    public DefaultHTMLRender createRender(boolean alwaysRenderDefault) {
        final DefaultHTMLRender render = new DefaultHTMLRender(alwaysRenderDefault);
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
        render.addNodeRender("list"     , new ListNodeRender());
        render.addNodeRender("section"  , new SectionRender());
        render.addNodeRender("template" , new TemplateNodeRender()); //TODO: avoid dependency with template package
        render.addNodeRender("table"    , new TableNodeRender());
        render.addKeyValueRender("url"        , new URLKeyValueRender());
        render.addKeyValueRender("archiveurl" , new URLKeyValueRender());
        render.addKeyValueRender("title"      , new TitleKeyValueRender());
        render.addKeyValueRender("content"    , new ContentKeyValueRender());

        render.addPrimitiveRender( new BaseTextPrimitiveNodeRender() );
        return render;
    }

    @Override
    public DefaultHTMLRender createRender() {
        return createRender(true);
    }

    private DefaultHTMLRenderFactory() {}

}
