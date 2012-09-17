package com.machinelinking.pagestruct;

import com.machinelinking.parser.DefaultWikiTextParserHandler;
import com.machinelinking.serializer.Serializer;

import java.net.URL;
import java.util.Stack;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextSerializerHandler extends DefaultWikiTextParserHandler {

    private final Serializer serializer;

    private final Stack<DocumentElement> documentStack = new Stack<>();

    protected WikiTextSerializerHandler(Serializer serializer) {
        if (serializer == null) throw new NullPointerException();
        this.serializer = serializer;
    }

    public void flush() {
        // Flushes intermediate structures if any.
    }

    @Override
    public void beginDocument(URL document) {
        serializer.openObject();
        serializer.fieldValue("__type", "page");
        serializer.fieldValue("url"   , document.toExternalForm());
        serializer.field("structure");
        serializer.openList();
    }

    @Override
    public void section(String title, int level) {
        serializer.openObject();
        serializer.fieldValue("__type", "section");
        serializer.fieldValue("title", title.trim());
        serializer.fieldValue("level", level);
        serializer.closeObject();
    }

    @Override
    public void reference(String label, String description) {
        serializer.openObject();
        serializer.fieldValue("__type", "reference");
        serializer.fieldValue("label", label);
        serializer.fieldValue("description", description);
        serializer.closeObject();
    }

    @Override
    public void link(String url, String description) {
        serializer.openObject();
        serializer.fieldValue("__type", "link");
        serializer.fieldValue("url", url);
        serializer.fieldValue("description", description);
        serializer.closeObject();
    }

    @Override
    public void beginList() {
        serializer.openObject();
        serializer.fieldValue("__type", "list");
        serializer.field("content");
        serializer.openList();
    }

    @Override
    public void listItem() {
        if( peekElement().getClass().equals(ListItem.class) ) {
            serializer.closeList();
            serializer.closeObject();
            popElement(ListItem.class);
        }

        pushElement( new ListItem() );
        serializer.openObject();
        serializer.fieldValue("__type", "list_item");
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
        serializer.fieldValue("__type", "template");
        serializer.fieldValue("name", templateId);
        serializer.field("content");
        serializer.openObject();
    }

    @Override
    public void parameter(String param) {
        if( peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }
        pushElement( new ParameterElement(param) );
        final String parameterName = param == null ? null : param.trim();
        serializer.field(parameterName);
        serializer.openList();
    }

    @Override
    public void text(String content) {
        if(content == null) return;
        content = content.trim();
        if(content.length() == 0) return;
        serializer.value(content);
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
        serializer.fieldValue("__type", "table");
        pushElement( new TableElement() );
        serializer.field("content");
        serializer.openList();
    }

    @Override
    public void headCell(int row, int col) {
        if (peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }
        if( peekElement().getClass().equals(TableCell.class) ) {
            serializer.closeList();
            serializer.closeObject();
            popElement(TableCell.class);
        }

        pushElement( new TableCell() );
        serializer.openObject();
        serializer.fieldValue("__type", "head_cell");
        serializer.field("content");
        serializer.openList();
    }

    @Override
    public void bodyCell(int row, int col) {
        if (peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }
        if( peekElement().getClass().equals(TableCell.class) ) {
            serializer.closeList();
            serializer.closeObject();
            popElement(TableCell.class);
        }

        pushElement( new TableCell() );
        serializer.openObject();
        serializer.fieldValue("__type", "body_cell");
        serializer.field("content");
        serializer.openList();
    }

    @Override
    public void endTable() {
        if( peekElement().getClass().equals(TableCell.class) ) {
            serializer.closeList();
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
        serializer.fieldValue("__type", "open_tag");
        serializer.fieldValue("name", name);
        serializeAttributes(attributes, serializer);
        serializer.closeObject();
    }

    @Override
    public void endTag(String name) {
        serializer.openObject();
        serializer.fieldValue("__type", "close_tag");
        serializer.fieldValue("name", name);
        serializer.closeObject();
    }

    @Override
    public void inlineTag(String name, Attribute[] attributes) {
        serializer.openObject();
        serializer.fieldValue("__type", "inline_tag");
        serializer.fieldValue("name", name);
        serializeAttributes(attributes, serializer);
        serializer.closeObject();
    }

    @Override
    public void commentTag(String comment) {
        serializer.openObject();
        serializer.fieldValue("__type", "comment_tag");
        serializer.fieldValue("comment", comment);
        serializer.closeObject();
    }

    @Override
    public void endDocument() {
        serializer.closeList();
        serializer.closeObject();
        serializer.flush();
    }

    protected String getTemplateId(String templateName) {
        return templateName.trim();
    }

    private void pushElement(DocumentElement de) {
        documentStack.push(de);
    }

    private void popElement(Class<? extends DocumentElement> ce) {
        final DocumentElement peek = documentStack.pop();
        if(! peek.getClass().equals(ce)) {
            throw new IllegalStateException(
                    String.format(
                            "Expected %s found %s",
                            ce.getSimpleName(),
                            peek.getClass().getSimpleName()
                    )
            );
        }
    }

    private DocumentElement peekElement() {
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

    class DocumentElement {
        private final String name;
        private DocumentElement(String name) {
            this.name = name;
        }
    }

    class TemplateElement extends DocumentElement {
        private TemplateElement(String name) {
            super(name);
        }
    }

    class ParameterElement extends DocumentElement {
        private ParameterElement(String name) {
            super(name);
        }
    }

    class TableElement extends DocumentElement {
        private TableElement() {
            super(null);
        }
    }

    class ListItem extends DocumentElement {
        private ListItem() {
            super(null);
        }
    }

    class TableCell extends DocumentElement {
        private TableCell() {
            super(null);
        }
    }

}
