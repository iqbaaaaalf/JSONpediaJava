package com.machinelinking.render;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultHTMLWriter implements HTMLWriter {

    private final Writer writer;

    private Stack<String> openTags = new Stack<>();

    public DefaultHTMLWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void openDocument() throws IOException {
        writer.append("<html>");
        writer.append("<head>");
        writer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        writer.append("<link href='http://fonts.googleapis.com/css?family=Aldrich' rel='stylesheet' type='text/css'/>");
        writer.append("<style type=\"text/css\">font-family: 'Aldrich', sans-serif;</style>");
        writer.append("</head>");
        writer.append("<body>");

    }

    @Override
    public void closeDocument() throws IOException {
        writer.append("</body></html>");
        if(!openTags.isEmpty()) throw new IllegalStateException();
    }

    @Override
    public void openTag(String tag, Map<String, String> attributes) throws IOException {
        writer.append('<');
        writer.append(tag);
        if(attributes != null) {
            for (Map.Entry<String, String> attribute : attributes.entrySet()) {
                writer.append(' ');
                writer.append(attribute.getKey());
                writer.append("=\"");
                writer.append(attribute.getValue());
                writer.append('"');
            }
        }
        writer.append('>');
        openTags.push(tag);
    }

    @Override
    public void openTag(String tag) throws IOException {
        openTag(tag, null);
    }

    @Override
    public void closeTag() throws IOException {
        final String tag;
        try {
            tag = openTags.pop();
        } catch (Exception e) {
            throw new RuntimeException("Too many close tags.", e);
        }
        writer.append("</");
        writer.append(tag);
        writer.append('>');
    }

    @Override
    public void text(String txt) throws IOException {
        writer.append(txt);
    }

    @Override
    public void openColorTag(final String color) throws IOException {
        openTag("font", new HashMap<String, String>(){{ put("color", color); }});
    }

    @Override
    public void key(String txt) throws IOException {
        writer.append( String.format("<small>%s</small>: ", txt) );
    }

    @Override
    public void anchor(String url, String text) throws IOException {
        writer.append( String.format("<a href=\"%s\">%s</a>", url, text) );
    }

    @Override
    public void image(String url, String text) throws IOException {
        writer.append( String.format("<a href=\"%s\"><img src=\"%s\"/ alt=\"%s\"/></a>", url, url, text) );
    }

    @Override
    public void openTable(String title) throws IOException {
        writer.append("<table style=\"border: solid 1px\">");
        writer.append("<caption>");
        writer.append(title);
        writer.append("</caption>");
    }

    @Override
    public void tableRow(String... cols) throws IOException {
        if(cols.length == 0) return;
        writer.append("<tr>");
        for(String col : cols) {
            writer.append("<td>");
            writer.append(col);
            writer.append("</td>");
        }
        writer.append("</tr>");
    }

    @Override
    public void closeTable() throws IOException {
        writer.append("</table>");
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

}
