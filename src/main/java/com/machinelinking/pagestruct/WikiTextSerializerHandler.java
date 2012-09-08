package com.machinelinking.pagestruct;

import com.machinelinking.extractor.Issue;
import com.machinelinking.parser.DefaultWikiTextParserHandler;
import com.machinelinking.parser.ParserLocation;
import com.machinelinking.serializer.Serializer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextSerializerHandler extends DefaultWikiTextParserHandler {

    private final Serializer serializer;

    private final Stack<DocumentElement> documentStack = new Stack<>();

    private List<Issue> issues;

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
    public void parseWarning(String msg, ParserLocation location) {
        if(issues == null) issues = new ArrayList<>();
        issues.add( new Issue(Issue.Type.Warning, msg, location) );
    }

    @Override
    public void parseError(Exception e, ParserLocation location) {
        if(issues == null) issues = new ArrayList<>();
        issues.add( new Issue(Issue.Type.Error, e.getMessage(), location) );
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
    public void templateParameterName(String param) {
        if( peekElement() instanceof ParameterElement) {
            popElement(ParameterElement.class);
            serializer.closeList();
        }
        pushElement( new ParameterElement(param) );
        final String parameterName = param.trim();
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
    public void endDocument() {
        serializer.closeList();

        serializeIssues(serializer);

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

    private void serializeIssues(Serializer serializer) {
        if(issues == null) return;
        serializer.field("issues");
        for(Issue issue : issues) {
            serializer.openObject();
            serializer.fieldValue("__type", issue.getType().name());
            serializer.fieldValue("msg", issue.getDescription());
            serializer.fieldValue("row", issue.getRow());
            serializer.fieldValue("col", issue.getRow());
            serializer.closeObject();
        }
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
