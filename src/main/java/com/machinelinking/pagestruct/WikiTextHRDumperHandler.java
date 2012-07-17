package com.machinelinking.pagestruct;

import com.machinelinking.parser.WikiTextParserHandler;

import java.net.URL;
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
    public void parseWarning(String msg, int row, int col) {
        printOut(String.format("Warning: %s (%d, %d)", msg, row, col));
        if(validating) throw new IllegalStateException("Unexpected warning.");
    }

    @Override
    public void parseError(Exception e, int row, int col) {
        printOut(String.format("Error: %s (%d, %d)", e, row, col));
        if(validating) throw new IllegalStateException("Unexpected error.");
    }

    @Override
    public void reference(String label, String description) {
        printOut(String.format("Reference: %s '%s'", label, description));
    }

    @Override
    public void link(String url, String description) {
        printOut(String.format("Link: %s '%s'", url, description));
    }

    @Override
    public void beginList() {
        printOut("Begin List");
    }

    @Override
    public void listItem() {
        printOut("List Item");
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
    public void templateParameterName(String param) {
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
    public void text(String content) {
        printOut("Text: " + ( content  == null ? "null" : "'" + content + "'"));
    }

    @Override
    public void startElement(String name) {
        printOut("Start Element: " + name);
    }

    @Override
    public void endElement(String name) {
        printOut("End Element: " + name);
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
