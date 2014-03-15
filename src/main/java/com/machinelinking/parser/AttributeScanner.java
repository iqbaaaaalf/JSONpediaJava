package com.machinelinking.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a tolerant scanner to read key value attributes like:
 * <ul>
 * <lu><code>key = value</code></li>
 * <lu><code>key = &quotes;value&quotes;</code></li>
 * </ul>
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class AttributeScanner {

    public static final char DEFAULT_SEPARATOR_CHAR = ' ';
    public static final char DEFAULT_ASSIGN_CHAR = '=';
    public static final char DEFAULT_VALUE_DELIMITER_CHAR = '"';

    public static Attribute[] scan(
            final char SEPARATOR, final char ASSIGN, final char VALUE_DELIMITER, final String content
    ) {
        final StringBuilder keyBuilder = new StringBuilder();
        final StringBuilder valueBuilder = new StringBuilder();
        final List<Attribute> attributes = new ArrayList<>();
        char c;
        for (int i = 0; i < content.length(); i++) {
            c = content.charAt(i);
            if (SEPARATOR == c) {
                // Empty.
            } else if (ASSIGN == c) {
                valueBuilder.delete(0, valueBuilder.length());
                i = scanValue(SEPARATOR, ASSIGN, VALUE_DELIMITER, content, i + 1, valueBuilder);
                attributes.add(new Attribute(keyBuilder.toString(), valueBuilder.toString()));
                keyBuilder.delete(0, keyBuilder.length());
            } else {
                keyBuilder.append(c);
            }
        }
        return attributes.toArray(new Attribute[attributes.size()]);
    }

    public static Attribute[] scan(String content) {
        return scan(DEFAULT_SEPARATOR_CHAR, DEFAULT_ASSIGN_CHAR, DEFAULT_VALUE_DELIMITER_CHAR, content);
    }

    // TODO: add escape support.
    protected static int scanValue(
            final char SEPARATOR, final char ASSIGN, final char VALUE_DELIMITER,
            final String content, final int index, final StringBuilder out
    ) {
        boolean withinQuotes = false;
        char c;
        int i;
        for (i = index; i < content.length(); i++) {
            c = content.charAt(i);
            if(ASSIGN == c) throw new IllegalArgumentException(
                    String.format("Invalid char %c at position %d", ASSIGN, i)
            );
            if (VALUE_DELIMITER == c) {
                if (withinQuotes) {
                    break;
                } else {
                    withinQuotes = true;
                }
            } else if (!withinQuotes && SEPARATOR == c) {
                if (out.length() > 0) {
                    return i;
                }
            } else {
                out.append(c);
            }
        }
        return i;
    }

    protected static int scanValue(final String content, final int index, final StringBuilder out) {
        return scanValue(
                DEFAULT_SEPARATOR_CHAR, DEFAULT_ASSIGN_CHAR, DEFAULT_VALUE_DELIMITER_CHAR, content, index, out
        );
    }

}
