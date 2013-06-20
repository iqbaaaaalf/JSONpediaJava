package com.machinelinking.pagestruct;

import com.machinelinking.parser.ParserLocation;
import com.machinelinking.parser.WikiTextParserHandler;

import java.net.URL;
import java.util.Arrays;
import java.util.Stack;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextHRDumperHandler implements WikiTextParserHandler {

    private final Stack<String> eventStack = new Stack<>();
    private final StringBuilder outBuilder = new StringBuilder();

    private final boolean validating;

    public WikiTextHRDumperHandler(boolean validating) {
        this.validating = validating;
    }

    public WikiTextHRDumperHandler() {
        this(true);
    }

    public boolean isValidating() {
        return validating;
    }

    public String getContent() {
        return outBuilder.toString();
    }

    public boolean isEventStackEmpty() {
        return eventStack.isEmpty();
    }

    @Override
    public void beginDocument(URL document) {
        printOut("Begin Document");
    }

    @Override
    public void section(String title, int level) {
        printOut(String.format("Section [%d] %s", level, title));
    }

    @Override
    public void endDocument() {
        printOut("End Document");
        if(validating && ! eventStack.isEmpty() ) throw new IllegalStateException();
    }

    @Override
    public void parseWarning(String msg, ParserLocation location) {
        final String err = String.format("Warning: %s (%d, %d)", msg, location.getRow(), location.getCol());
        printOut(err);
        if(validating) throw new IllegalStateException("Unexpected warning: " + err);
    }

    @Override
    public void parseError(Exception e, ParserLocation location) {
        printOut(String.format("Error: %s (%d, %d)", e, location.getRow(), location.getCol()));
        if(validating) throw new IllegalStateException("Unexpected error.");
    }

    @Override
    public void beginReference(String label) {
        printOut("Begin Reference: " + label);
    }

    @Override
    public void endReference(String label) {
        printOut("End Reference: " + label);
    }

    @Override
    public void beginLink(URL url) {
        printOut(String.format("Begin Link: %s", url));
    }

    @Override
    public void endLink(URL url) {
        printOut(String.format("End Link: %s", url));
    }

    @Override
    public void beginList() {
        printOut("Begin List");
    }

    @Override
    public void listItem(ListType t, int level) {
        printOut( String.format("List Item: %s %d", t, level) );
    }

    @Override
    public void endList() {
        printOut("End List");
    }

    @Override
    public void beginTemplate(String name) {
        printOut("Begin Template: " + name);
    }

    @Override
    public void parameter(String param) {
        printOut("k: " + param);
    }

    @Override
    public void endTemplate(String name) {
        printOut("End Template: " + name);
    }

    @Override
    public void beginTable() {
        printOut("Begin Table");
    }

    @Override
    public void headCell(int row, int col) {
        printOut( String.format("Header Cell (%d, %d)", row, col) );
    }

    @Override
    public void bodyCell(int row, int col) {
        printOut( String.format("Body Cell (%d, %d)", row, col) );
    }

    @Override
    public void endTable() {
        printOut("End Table");
    }

    @Override
    public void beginTag(String node, Attribute[] attributes) {
        printOut( String.format("Open Tag: %s attributes: %s", node, Arrays.asList(attributes)) );
    }

    @Override
    public void endTag(String node) {
        printOut("Close Tag: " + node);
    }

    @Override
    public void inlineTag(String node, Attribute[] attributes) {
        printOut( String.format("Inline Tag: %s attributes: %s", node, Arrays.asList(attributes)) );
    }

    @Override
    public void commentTag(String comment) {
        printOut("Comment Tag: " + comment);
    }

    @Override
    public void text(String content) {
        printOut("Text: " + ( content  == null ? "null" : "'" + content + "'"));
    }

    @Override
    public void italicBold(int level) {
        printOut("ItalicBold: " + level);
    }

    //TODO: replace with ValidatingWikiTextParserHandler
    private void printOut(String msg) {
        if (validating) {
            String[] sections = msg.split(" ");
            String action = sections[0];
            if (action.toLowerCase().equals("begin")) {
                final String node = sections[1];
                eventStack.push(msg);
            } else if (action.toLowerCase().equals("end")) {
                final String expected = eventStack.pop();
                final String node = sections[1];
                if (!expected.split(" ")[1].equals(node)) {
                    throw new IllegalStateException(
                            String.format("Expected closure for action [%s], found [%s]", expected, node)
                    );
                }
            }
        }

        outBuilder.append(msg).append('\n');
        System.out.println(msg);
    }

}
