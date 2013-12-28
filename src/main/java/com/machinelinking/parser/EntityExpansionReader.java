package com.machinelinking.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class EntityExpansionReader extends Reader {

    private final short MAX_ENTITY_NUMERIC_SIZE = 6;

    private final BufferedReader inner;

    private final StringBuilder entityContentBuffer = new StringBuilder();

    private CharBuffer charBuffer = null;

    @Override
    public void mark(int readAheadLimit) throws IOException {
        inner.mark(readAheadLimit);
    }

    @Override
    public void reset() throws IOException {
        inner.reset();
    }

    public EntityExpansionReader(BufferedReader inner) {
        this.inner = inner;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int intc;
        char c;
        int writtenChars = 0;
        boolean foundTerminal = false;
        for (int i = off; i < len; i++) {
            //intc = inner.read();
            intc = internalRead();
            if (intc == -1) break;
            c = (char) intc;
            if (c == '&') {
                entityContentBuffer.delete(0, entityContentBuffer.length());
                for(int l = 0; l < MAX_ENTITY_NUMERIC_SIZE; l++) {
                    intc = inner.read();
                    if (intc == -1) break;
                    c = (char) intc;
                    if (c == ';') {
                        foundTerminal = true;
                        break;
                    }
                    entityContentBuffer.append(c);
                }
                final String entityContent = entityContentBuffer.toString();
                if(foundTerminal && entityContent.length() > 0) {
                    final Character entity = convertToChar(entityContent);
                    if(entity != null) {
                        cbuf[i] = entity;
                        writtenChars++;
                    } else { // One char more to read.
                        i--;
                    }
                } else { // &something incomplete.
                    cbuf[i++] = '&';
                    writtenChars++;
                    final int flushed = flushToBuffer(cbuf, i, entityContent);
                    final String delta = entityContent.substring(flushed);
                    if(delta.length() > 0) internalWrite(delta);
                    i += flushed - 1; // to compensate the final increment.
                    writtenChars += flushed;
                }
            } else {
                cbuf[i] = c;
                writtenChars++;
            }
        }
        return writtenChars == 0 ? -1 : writtenChars;
    }

    @Override
    public void close() throws IOException {
        this.inner.close();
    }

    private int internalRead() throws IOException {
        if(charBuffer != null && charBuffer.hasRemaining()) {
            return charBuffer.get();
        } else {
            return inner.read();
        }
    }

    private void internalWrite(String content) {
        if(charBuffer == null) {
            charBuffer = CharBuffer.wrap(content);
        } else {
            charBuffer.put(content);
        }
    }

    private char entityNameToChar(String name) {
        switch (name) {
            case "nbsp":
                return ' ';
            default:
                throw new IllegalArgumentException("Unknown entity name: " + name);
        }
    }

    private Character convertToChar(String entitySequence) {
        final char type = entitySequence.charAt(0);
        if (type == '#') { // &#nnnn; OR &#xhhhh;
            if (entitySequence.length() > 1) {
                char result;
                if (entitySequence.charAt(1) == 'x') {
                    result = (char) Long.parseLong(entitySequence.substring(2), 16);
                } else {
                    result = (char) Integer.parseInt(entitySequence.substring(1));
                }
                return result;
            } else {
                return null;
            }
        } else { // &name;
            return entityNameToChar(entitySequence);
        }
    }

    private int flushToBuffer(char[] buffer, int start, CharSequence sequence) {
        int i;
        final int limit = Math.min(buffer.length, start + sequence.length());
        for(i = start; i < limit; i++)
            buffer[i] = sequence.charAt(i - start);
        return i - start;
    }

}
