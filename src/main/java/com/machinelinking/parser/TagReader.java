package com.machinelinking.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Utility class to manage <i>HTML</i> markup.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TagReader {

    private final StringBuilder tagContent = new StringBuilder();
    private final WikiTextParserHandler handler;

    private final Stack<StackElement> tagStack = new Stack<>();

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

    protected static TagHandler.Attribute[] attributeKeyValueScanner(String content) {
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
                attributes.add( new TagHandler.Attribute(keyBuilder.toString(), valueBuilder.toString()));
                keyBuilder.delete(0, keyBuilder.length());
            } else {
                keyBuilder.append(c);
            }
        }
        return attributes.toArray( new TagHandler.Attribute[attributes.size()]);
    }

    public TagReader(WikiTextParserHandler handler) {
        this.handler = handler;
    }

    public boolean isInsideNode() {
        return ! tagStack.isEmpty();
    }

    public boolean isInsideNode(String... names) {
        if(tagStack.empty()) return false;
        final String peek = tagStack.peek().node;
        for(String name : names) {
            if(peek.equals(name)) return true;
        }
        return false;
    }

    public List<StackElement> getStack() {
        return Collections.unmodifiableList(tagStack);
    }

    public void readNode(ParserReader r) throws IOException {
        boolean closeTag       = false;
        boolean waitingTagName = true;
        String tagName         = null;
        int cursorPosition     = -1;
        tagContent.delete(0, tagContent.length());

        char c;
        c = r.read();
        cursorPosition++;
        if(c != '<') throw new IllegalStateException();

        while (true) {
            r.mark();
            c = r.read();
            cursorPosition++;

            if(cursorPosition == 1 && '!' == c) {
                c = r.read();
                cursorPosition++;
                if ('-' == c) {
                    c = r.read();
                    cursorPosition++;
                    if ('-' == c) {
                        readUntilCloseComment(r);
                    } else {
                        handler.parseWarning("Invalid begin tag sequence: '<-" + c + "'", r.getLocation());
                    }
                }
                break;
            }

            if (c == ' ' && waitingTagName) {
                tagName = tagContent.toString();
                tagContent.delete(0, tagContent.length());
                waitingTagName = false;
            } else if (c == '/') {
                if (cursorPosition == 1) {
                    closeTag = true;
                } else { // Inline
                    c = r.read();
                    cursorPosition++;
                    if('>' == c) {
                        if (waitingTagName) {
                            tagName = tagContent.toString();
                            tagContent.delete(0, tagContent.length());
                            waitingTagName = false;
                        }
                        final String content = tagContent.toString();
                        handler.inlineTag(tagName, attributeKeyValueScanner(content));
                    } else {
                        handler.parseWarning("Sequence error in tag", r.getLocation());
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
                    popTag(tagName, r.getLocation());
                } else {
                    pushTag(tagName, attributeKeyValueScanner(content));
                }
                break;
            } else if(waitingTagName && ! Character.isJavaIdentifierPart(c)) {
                r.reset();
                break;
            } else {
                tagContent.append(c);
            }
        }
    }

    final StringBuilder insideNodeTagSB = new StringBuilder();
    public void readUntilNextTag(ParserReader r) throws IOException {
        char c;
        while(true) {
            c = r.read();
            if('<' == c) {
                final String content = insideNodeTagSB.toString();
                insideNodeTagSB.delete(0, insideNodeTagSB.length());
                handler.text(content);
                r.reset();
                break;
            } else {
                insideNodeTagSB.append(c);
                r.mark();
            }
        }
    }

    protected void pushTag(String name, WikiTextParserHandler.Attribute[] attributes) {
        tagStack.push( new StackElement(name, attributes) );
        handler.beginTag(name, attributes);
    }

    protected void popTag(String name, ParserLocation location) {
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
            handler.parseWarning( String.format("Tag closure [%s] has never opened.", name), location);
        } else {
            for (int j = 0; j < popUntil; j++) {
                tagStack.pop();
            }
            handler.endTag(name);
        }
    }

    final StringBuilder commentSB = new StringBuilder();
    private void readUntilCloseComment(ParserReader r) throws IOException {
        commentSB.delete(0, commentSB.length());
        while(true) {
            char c = r.read();
            if(c == '-') {
                char c1 = r.read();
                if('-' == c1) {
                    char c2 = r.read();
                    if ('>' == c2) {
                        handler.commentTag(commentSB.toString());
                        commentSB.delete(0, commentSB.length());
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
        private final TagHandler.Attribute[] attributes;

        StackElement(String node, WikiTextParserHandler.Attribute[] attributes) {
            this.node = node;
            this.attributes = attributes;
        }

        public String getNode() {
            return node;
        }

        public List<TagHandler.Attribute> getAttributes() {
            return Collections.unmodifiableList( Arrays.asList(attributes) );
        }

        @Override
        public String toString() {
            return String.format("node: %s attributes: %s", node, Arrays.asList(attributes));
        }
    }

}
