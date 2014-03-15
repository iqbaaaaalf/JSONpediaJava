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

    public static Attribute[] scan(String content) {
        final StringBuilder keyBuilder = new StringBuilder();
        final StringBuilder valueBuilder = new StringBuilder();
        final List<Attribute> attributes = new ArrayList<>();
        char c;
        for (int i = 0; i < content.length(); i++) {
            c = content.charAt(i);
            if (' ' == c) {
                // Empty.
            } else if ('=' == c) {
                valueBuilder.delete(0, valueBuilder.length());
                i = scanValue(content, i + 1, valueBuilder);
                attributes.add(new Attribute(keyBuilder.toString(), valueBuilder.toString()));
                keyBuilder.delete(0, keyBuilder.length());
            } else {
                keyBuilder.append(c);
            }
        }
        return attributes.toArray(new Attribute[attributes.size()]);
    }

    protected static int scanValue(String content, int index, final StringBuilder out) {
        boolean withinQuotes = false;
        char c;
        int i;
        for (i = index; i < content.length(); i++) {
            c = content.charAt(i);
            if ('"' == c) {
                if (withinQuotes) {
                    break;
                } else {
                    withinQuotes = true;
                }
            } else if (!withinQuotes && ' ' == c) {
                if (out.length() > 0) {
                    return i;
                }
            } else {
                out.append(c);
            }
        }
        return i;
    }

}
