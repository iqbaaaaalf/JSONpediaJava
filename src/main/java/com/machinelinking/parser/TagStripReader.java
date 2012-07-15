package com.machinelinking.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TagStripReader extends Reader {

    private final Stack<String> openTags = new Stack<>();
    private final StringBuilder insideTagSB = new StringBuilder();
    private final StringBuilder tagSB       = new StringBuilder();

    private final BufferedReader wrapped;

    private boolean beginTag, closeTag, insideTag = false;
    private int intc;
    private char c;

    public TagStripReader(Reader wrapped) {
        this.wrapped = new BufferedReader(wrapped);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int i = 0;
        while(i < len) {
            intc = wrapped.read();
            if(intc == -1) return i == 0 ? -1 : i;
            c = (char) intc;
            if(c == '<') {
                beginTag = true;
                closeTag = false;
            } else if(beginTag && c == '>') {
                beginTag = false;
                final String currentTag = tagSB.toString();
                tagSB.delete(0, tagSB.length());
                System.out.println("TAG: " + currentTag);
                if(closeTag) {
                    if(checkOutOfTags(currentTag)) {
                        insideTag = false;
                        System.out.println("INSIDE: " + insideTagSB.toString());
                        insideTagSB.delete(0, insideTagSB.length());
                    }
                } else {
                    insideTag = true;
                    openTags.push(currentTag);
                }
            } else if(beginTag && c == '/') {
                closeTag = true;
            } else {
                if (insideTag && !beginTag) {
                    insideTagSB.append(c);
                }
                if(beginTag) {
                    tagSB.append(c);
                }
                if(! insideTag && !beginTag) {
                    cbuf[off + i++] = c;
                }
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public void close() throws IOException {
        wrapped.close();
    }

    private boolean checkOutOfTags(String tag) {
        while(!openTags.isEmpty()) {
            final String peekTag = openTags.pop();
            if(peekTag.equals(tag)) {
                break;
            }
        }
        return openTags.isEmpty();
    }

}
