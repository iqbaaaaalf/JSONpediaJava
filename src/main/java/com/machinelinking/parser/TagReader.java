package com.machinelinking.parser;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TagReader {

    private final StringBuilder tagContent = new StringBuilder();
    private final WikiTextParserHandler handler;

    private final Stack<StackElement> tagStack = new Stack<>();

    private int cursorPosition;
    private boolean closeTag;
    private boolean waitingTagName;
    private String tagName;

    protected static int attributeValueScanner(String content, int index, final StringBuilder out) {
        boolean withinQuotes = false;
        char c;
        int i;
        for(i = index; i < content.length(); i++) {
            c = content.charAt(i) ;
            if('"' == c) {
                if(withinQuotes) {
                    break;
                } else {
                    withinQuotes = true;
                }
            } else if(!withinQuotes && ' ' == c) {
                if(out.length() > 0) {
                    return i;
                }
            } else {
                out.append(c);
            }
        }
        return i;
    }

    protected static WikiTextParserHandler.Attribute[] attributeKeyValueScanner(String content) {
        final StringBuilder keyBuilder = new StringBuilder();
        final StringBuilder valueBuilder = new StringBuilder();
        final List<WikiTextParserHandler.Attribute> attributes = new ArrayList<>();
        char c;
        for(int i = 0; i < content.length(); i++) {
            c = content.charAt(i);
            if(' ' == c) {
                // Empty.
            } else if('=' == c) {
                valueBuilder.delete(0, valueBuilder.length());
                i = attributeValueScanner(content, i + 1, valueBuilder);
                attributes.add( new WikiTextParserHandler.Attribute(keyBuilder.toString(), valueBuilder.toString()));
                keyBuilder.delete(0, keyBuilder.length());
            } else {
                keyBuilder.append(c);
            }
        }
        return attributes.toArray( new WikiTextParserHandler.Attribute[attributes.size()]);
    }

    public TagReader(WikiTextParserHandler handler) {
        this.handler = handler;
    }

    public boolean isInsideNode() {
        return ! tagStack.isEmpty();
    }

    public List<StackElement> getStack() {
        return Collections.unmodifiableList(tagStack);
    }

    public void readNode(Reader r) throws IOException {
        tagContent.delete(0, tagContent.length());
        closeTag = false;
        waitingTagName = true;
        tagName = null;
        cursorPosition = 0;

        char c;

        c = read(r);
        if(c != '<') throw new IllegalStateException();

        while (true) {
            c = read(r);

            if('-' == c && waitingTagName) {
                c = read(r);
                if('-' == c) {
                    readUntilCloseComment(r);
                } else {
                    handler.parseWarning("Invalid begin tag sequence: '<-" + c + "'", -1, -1); // TODO
                }
                break;
            } else if (c == ' ' && waitingTagName) {
                tagName = tagContent.toString();
                tagContent.delete(0, tagContent.length());
                waitingTagName = false;
            } else if (c == '/') {
                if (cursorPosition == 0) {
                    closeTag = true;
                } else { // Inline
                    c = read(r);
                    if('>' == c) {
                        if (waitingTagName) {
                            tagName = tagContent.toString();
                            tagContent.delete(0, tagContent.length());
                            waitingTagName = false;
                        }
                        final String content = tagContent.toString();
                        handler.inlineTag(tagName, attributeKeyValueScanner(content));
                    } else {
                        handler.parseWarning("Sequence error in tag", -1, -1); // TODO: propagare row / col
                    }
                    break;
                }
            } else if (c == '>') {
                if(waitingTagName) {
                    tagName = tagContent.toString();
                    tagContent.delete(0, tagContent.length());
                    waitingTagName = false;
                }
                final String content = tagContent.toString();
                if(closeTag) {
                    popTag(tagName);
                } else {
                    pushTag(tagName, attributeKeyValueScanner(content));
                }
                //System.out.printf("Tag name: [%s], open: %b, content:[%s]\n", tagName, !closeTag, content);
                break;
            } else {
                tagContent.append(c);
            }
            cursorPosition++;
        }
    }

    protected void pushTag(String name, WikiTextParserHandler.Attribute[] attributes) {
        tagStack.push( new StackElement(name, attributes) );
        handler.beginTag(name, attributes);
    }

//    protected void popTag(String name) {
//        final StackElement peek = tagStack.isEmpty() ? null : tagStack.peek();
//        if(peek != null && peek.node.equals(name)) {
//            tagStack.pop();
//        } else {
//            handler.parseWarning( String.format("Tag closure [%s] has never opened.", name), -1 , -1 ); // TODO: this should be active.
//        }
//        handler.endTag(name);
//    }

    protected void popTag(String name) {
        if( tagStack.isEmpty() ) return;
        final int currStackSize = tagStack.size();
        int popUntil = -1;
        for(int i = currStackSize - 1; i >=0; i--) {
            if(name.equals(tagStack.get(i).node)) {
                popUntil = currStackSize - i;
                break;
            }
        }
        if(popUntil == -1) {
            handler.parseWarning( String.format("Tag closure [%s] has never opened.", name), -1 , -1 ); // TODO
        } else {
            for (int j = 0; j < popUntil; j++) {
                tagStack.pop();
            }
            handler.endTag(name);
        }
    }

    private char read(Reader r) throws IOException {
        int intc = r.read();
        if(intc == -1) throw new EOFException();
        return  (char) intc;
    }

    final StringBuilder commentSB = new StringBuilder();
    private void readUntilCloseComment(Reader r) throws IOException {
        commentSB.delete(0, commentSB.length());
        while(true) {
            char c = read(r);
            if(c == '-') {
                char c1 = read(r);
                if('-' == c1) {
                    char c2 = read(r);
                    if ('>' == c2) {
                        handler.commentTag(commentSB.toString());
                        commentSB.delete(0, commentSB.length());
                    } else {
                        handler.parseWarning("Invalid char '-' within comment tag.", -1, -1); // TODO
                    }
                    break;
                } else {
                    commentSB.append(c);
                    commentSB.append(c1);
                }
            } else {
                commentSB.append(c);
            }
        }
    }

    public class StackElement {
        private final String node;
        private final WikiTextParserHandler.Attribute[] attributes;

        StackElement(String node, WikiTextParserHandler.Attribute[] attributes) {
            this.node = node;
            this.attributes = attributes;
        }

        public String getNode() {
            return node;
        }

        public List<WikiTextParserHandler.Attribute> getAttributes() {
            return Collections.unmodifiableList( Arrays.asList(attributes) );
        }

        @Override
        public String toString() {
            return String.format("node: %s attributes: %s", node, Arrays.asList(attributes));
        }
    }
}
