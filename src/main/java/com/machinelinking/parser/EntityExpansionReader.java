package com.machinelinking.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class EntityExpansionReader extends Reader {

    private final short MAX_ENTITY_NUMERIC_SIZE = 6;

    private final BufferedReader inner;

    private final StringBuilder intBuffer = new StringBuilder();

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
        int charsRead = 0;
        boolean foundTerminal = false;
        int i;
        for (i = off; i < len; i++) {
            intc = inner.read();
            if (intc == -1) break;
            c = (char) intc;
            if (c == '&') {
                intBuffer.delete(0, intBuffer.length());
                for(int l = 0; l < MAX_ENTITY_NUMERIC_SIZE; l++) {
                    intc = inner.read();
                    if (intc == -1) break;
                    c = (char) intc;
                    if (c == ';') {
                        foundTerminal = true;
                        break;
                    }
                    intBuffer.append(c);
                }
                if(foundTerminal && intBuffer.length() > 0) {
                    final String entitySequence = intBuffer.toString();
                    final char type = entitySequence.charAt(0);
                    if(type == '#') { // &#nnnn; OR &#xhhhh;
                        if (entitySequence.length() > 1) {
                            if (entitySequence.charAt(1) == 'x') {
                                cbuf[i] = (char) Long.parseLong(entitySequence.substring(2), 16);
                            } else {
                                cbuf[i] = (char) Integer.parseInt(entitySequence.substring(1));
                            }
                            charsRead++;
                        } else {
                            i--;
                        }
                    } else { // &aaaa;
                        final int bufferLength = intBuffer.length();
                        cbuf[i++] = '&';
                        int k;
                        for(k = 0; k < bufferLength; k++) cbuf[i+k] = intBuffer.charAt(k);
                        cbuf[k+i] = ';';
                        charsRead += bufferLength + 2;
                        i += bufferLength;
                    }
                } else { // &something else...
                    cbuf[i++] = '&';
                    final int bufferLength = intBuffer.length();
                    if (!foundTerminal && bufferLength > 0) {
                        int k;
                        for (k = 0; k < bufferLength; k++) cbuf[i + k] = intBuffer.charAt(k);
                        charsRead += bufferLength + 1;
                        i = k + 1;
                    }
                }
            } else {
                cbuf[i] = c;
                charsRead++;
            }
        }
        return charsRead == 0 ? -1 : charsRead;
    }

    @Override
    public void close() throws IOException {
        this.inner.close();
    }

    private boolean isHexLetter(char c) {
        return ('a' <= c && c <= 'f') ||  ('A' <= c && c <= 'F');
    }

}
