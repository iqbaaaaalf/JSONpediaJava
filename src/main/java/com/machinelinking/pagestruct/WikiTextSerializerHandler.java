package com.machinelinking.pagestruct;

import com.machinelinking.parser.Attribute;
import com.machinelinking.parser.DefaultWikiTextParserHandler;
import com.machinelinking.serializer.Serializer;

import java.net.URL;
import java.util.Stack;

/**
 * {@link WikiTextSerializerHandler} converting parser events to serialization events
 * using a {@link Serializer}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextSerializerHandler extends DefaultWikiTextParserHandler {

    public static final String TYPE_FIELD = "__type";

    private final Serializer serializer;

    private final Stack<Element> documentStack = new Stack<>();

    private boolean isItalicBoldOpen;

    protected WikiTextSerializerHandler(Serializer serializer) {
        if (serializer == null) throw new NullPointerException();
        this.serializer = serializer;
    }

    public void flush() {
        serializer.flush();
    }

    public void reset() {
        isItalicBoldOpen = false;
        clearStack();
        pushElement(new DocumentElement());
    }

    @Override
    public void beginDocument(URL document) {
        reset();
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "page");
        serializer.fieldValue("url"   , document.toExternalForm());
        serializer.field("structure");
        serializer.openList();
    }

    @Override
    public void var(Var v) {
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "var");
        serializer.fieldValue("name"   , v.name);
        serializer.fieldValue("default", v.defaultValue);
        serializer.closeObject();
    }

    @Override
    public void paragraph() {
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "paragraph");
        serializer.closeObject();
    }

    @Override
    public void section(String title, int level) {
        popUntilElement(DocumentElement.class);
        pushElement(new Section(String.format("%s - %d", title, level)));
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "section");
        serializer.fieldValue("title", title.trim());
        serializer.fieldValue("level", level);
        serializer.field("content");
        serializer.openObject();
    }

    @Override
    public void beginReference(String label) {
        pushElement( new Reference(label) );
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "reference");
        serializer.fieldValue("label", label);
        serializer.field("content");
        serializer.openObject();
    }

    @Override
    public void endReference(String label) {
        if (peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }

        serializer.closeObject();
        serializer.closeObject();
        popElement(Reference.class);
    }

    @Override
    public void beginLink(URL url) {
        final String urlStr = url.toExternalForm();
        pushElement( new Link(urlStr) );
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "link");
        serializer.fieldValue("url", urlStr);
        serializer.field("content");
        serializer.openObject();
    }

    @Override
    public void endLink(URL url) {
        if (peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }

        serializer.closeObject();
        serializer.closeObject();
        popElement(Link.class);
    }

    @Override
    public void beginList() {
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "list");
        serializer.field("content");
        serializer.openList();
    }

    @Override
    public void listItem(ListType t, int level) {
        if( peekElement().getClass().equals(ListItem.class) ) {
            serializer.closeList();
            serializer.closeObject();
            popElement(ListItem.class);
        }

        pushElement( new ListItem() );
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "list_item");
        serializer.fieldValue("level", level);
        serializer.fieldValue("item_type", t.name());
        serializer.field("content");
        serializer.openList();
    }

    @Override
    public void endList() {
        // Close list item if any.
        if( peekElement().getClass().equals(ListItem.class) ) {
            serializer.closeList();
            serializer.closeObject();
            popElement(ListItem.class);
        }

        serializer.closeList();
        serializer.closeObject();
    }

    @Override
    public void beginTemplate(String name) {
        final String templateId = getTemplateId(name);
        pushElement( new TemplateElement(templateId) );
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "template");
        serializer.fieldValue("name", templateId);
        serializer.field("content");
        serializer.openObject();
    }

    @Override
    public void endTemplate(String name) {
        // Close last param if any.
        if (peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }

        serializer.closeObject(); // content
        serializer.closeObject(); // template
        popElement(TemplateElement.class);
    }

    @Override
    public void beginTable() {
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "table");
        pushElement(new TableElement());
    }

    @Override
    public void headCell(int row, int col) {
        if(peekElement() instanceof TableElement) {
            serializer.field("content");
            serializer.openList();
        }

        if (peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }

        if( peekElement().getClass().equals(TableCell.class) ) {
            serializer.closeObject();
            serializer.closeObject();
            popElement(TableCell.class);
        }

        pushElement(new TableCell());
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "head_cell");
        serializer.field("content");
        serializer.openObject();
    }

    @Override
    public void bodyCell(int row, int col) {
        if (peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }

        if( peekElement().getClass().equals(TableCell.class) ) {
            serializer.closeObject();
            serializer.closeObject();
            popElement(TableCell.class);
        }

        pushElement( new TableCell() );
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "body_cell");
        serializer.field("content");
        serializer.openObject();
    }

    @Override
    public void endTable() {
        if (peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }

        if( peekElement().getClass().equals(TableCell.class) ) {
            serializer.closeObject();
            serializer.closeObject();
            popElement(TableCell.class);
        }

        serializer.closeList();
        serializer.closeObject();
        popElement(TableElement.class);
    }

    @Override
    public void beginTag(String name, Attribute[] attributes) {
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "open_tag");
        serializer.fieldValue("name", name);
        serializeAttributes(attributes, serializer);
        serializer.closeObject();
    }

    @Override
    public void endTag(String name) {
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "close_tag");
        serializer.fieldValue("name", name);
        serializer.closeObject();
    }

    @Override
    public void inlineTag(String name, Attribute[] attributes) {
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "inline_tag");
        serializer.fieldValue("name", name);
        serializeAttributes(attributes, serializer);
        serializer.closeObject();
    }

    @Override
    public void commentTag(String comment) {
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "comment_tag");
        serializer.fieldValue("comment", comment);
        serializer.closeObject();
    }

    @Override
    public void parameter(String param) {
        if( peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }
        pushElement(new ParameterElement(param));
        final String parameterName = param == null ? null : param.trim();
        serializer.field(parameterName);
        serializer.openList();
    }

    @Override
    public void entity(String form, char value) {
        serializer.openObject();
        serializer.fieldValue(TYPE_FIELD, "entity");
        serializer.fieldValue("form", form);
        serializer.fieldValue("value", "" + value);
        serializer.closeObject();
    }

    @Override
    public void italicBold(int level) {
        isItalicBoldOpen = !isItalicBoldOpen;
        final String tag = level > 2 ? "b" : "i";
        text( String.format("<%s%s>", isItalicBoldOpen ? "" : "/", tag ) );
    }

    @Override
    public void text(String content) {
        if(peekElement() instanceof TableElement) {
            serializer.fieldValue("header", content);
            return;
        }
        if(content == null) return;
        content = content.trim();
        if(content.length() == 0) return;
        serializer.value(content);
    }

    @Override
    public void endDocument() {
        popElement(Section.class, false);
        popElement(DocumentElement.class);
        serializer.closeList();
        serializer.closeObject();
        serializer.flush();
    }

    protected String getTemplateId(String templateName) {
        return templateName.trim();
    }

    private void clearStack() {
        documentStack.clear();
    }

    private void pushElement(Element de) {
        documentStack.push(de);
    }

    private void popElement(Class<? extends Element> ce, boolean mustBePresent) {
        final Element peek = documentStack.peek();
        if (peek.getClass().equals(ce)) {
            documentStack.pop();
        } else if (mustBePresent) {
            throw new IllegalStateException(
                    String.format(
                            "Expected %s found %s",
                            ce.getSimpleName(),
                            peek.getClass().getSimpleName()
                    )
            );
        }
    }

    private void popElement(Class<? extends Element> ce) {
        popElement(ce, true);
    }

    private void popUntilElement(Class<? extends Element> ce) {
        while(true) {
            final Element peek = documentStack.peek();
            if(peek.getClass().equals(ce)) {
                break;
            }
            documentStack.pop();
            peek.close(serializer);
        }
    }

    private Element peekElement() {
        return documentStack.peek();
    }

    private void serializeAttributes(Attribute[] attributes, Serializer serializer) {
        serializer.field("attributes");
        serializer.openList();
        for(Attribute attribute : attributes) {
            serializer.openObject();
            serializer.fieldValue(attribute.name, attribute.value);
            serializer.closeObject();
        }
        serializer.closeList();
    }

    abstract class Element {
        private final String name;
        private Element(String name) {
            this.name = name;
        }
        abstract void close(Serializer s);
    }

    class DocumentElement extends Element {
        private DocumentElement() {
            super(null);
        }
        @Override
        void close(Serializer s) {
            throw new UnsupportedOperationException();
        }
    }

    class Section extends Element {
        private Section(String name) {
            super(name);
        }
        @Override
        void close(Serializer s) {
            s.closeObject();
            s.closeObject();
        }
    }

    class TemplateElement extends Element {
        private TemplateElement(String name) {
            super(name);
        }
        @Override
        void close(Serializer s) {
            throw new UnsupportedOperationException();
        }
    }

    class ParameterElement extends Element {
        private ParameterElement(String name) {
            super(name);
        }
        @Override
        void close(Serializer s) {
            s.closeList();
        }
    }

    class TableElement extends Element {
        private TableElement() {
            super(null);
        }
        @Override
        void close(Serializer s) {
            s.closeObject();
        }
    }

    class ListItem extends Element {
        private ListItem() {
            super(null);
        }
        @Override
        void close(Serializer s) {
            throw new UnsupportedOperationException();
        }
    }

    class TableCell extends Element {
        private TableCell() {
            super(null);
        }
        @Override
        void close(Serializer s) {
            s.closeObject();
        }
    }

    class Reference extends Element {
        private Reference(String name) {
            super(name);
        }
        @Override
        void close(Serializer s) {
            throw new UnsupportedOperationException();
        }
    }

    class Link extends Element {
        private Link(String name) {
            super(name);
        }
        @Override
        void close(Serializer s) {
            throw new UnsupportedOperationException();
        }
    }

}
