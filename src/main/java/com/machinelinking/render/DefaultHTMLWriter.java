package com.machinelinking.render;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Default implementation of {@link HTMLWriter}.
 *
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
        writer.append(loadResource("default-html-writer-header.html"));
        writer.append("<body>");
        writer.append("<script type=\"text/javascript\">");
        writer.append(loadResource("default-html-writer-include.js"));
        writer.append("</script>");
        writer.append(loadResource("default-html-writer-body-open.html"));
    }

    @Override
    public void closeDocument() throws IOException {
        writer.append( loadResource("default-html-writer-body-close.html"));
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
    public void heading(int level, String title) throws IOException {
        if(level > 6) level = 6;
        writer.append(String.format("<h%d>%s</h%d>", level, title, level));
    }

    @Override
    public void text(String txt) throws IOException {
        writer.append( escapeStringMarkup(txt) );
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
    public void anchor(String url, String text, boolean internal) throws IOException {
        if(internal) {
            url = "/annotate/resource/html/" + URLEncoder.encode(url, "utf-8");
        }
        writer.append( String.format("<a href=\"%s\">%s</a>", url, text) );
    }

    @Override
    public void image(String url, String text) throws IOException {
        writer.append( String.format("<a href=\"%s\"><img src=\"%s\"/ alt=\"%s\"/></a>", url, url, text) );
    }

    @Override
    public void openTable(String title, Map<String,String> attributes) throws IOException {
        final Map<String,String> finalAttrs = new HashMap<>(
                attributes == null ? Collections.<String,String>emptyMap() : attributes
        );
        merge("style", "border: 1px solid black", finalAttrs);
        openTag("table", finalAttrs);
        writer.append("<caption>");
        writer.append(title);
        writer.append("</caption>");
    }

    @Override
    public void openTableRow() throws IOException {
        writer.append("<tr>");
    }

    @Override
    public void closeTableRow() throws IOException {
        writer.append("</tr>");
    }

    @Override
    public void openTableCol() throws IOException {
        writer.append("<td>");
    }

    @Override
    public void closeTableCol() throws IOException {
        writer.append("</td>");
    }

    @Override
    public void tableRow(String... cols) throws IOException {
        if(cols.length == 0) return;
        openTableRow();
        for(String col : cols) {
            openTableCol();
            writer.append(col);
            closeTableCol();
        }
        closeTableRow();
    }

    @Override
    public void closeTable() throws IOException {
        closeTag();
    }

    @Override
    public void br() throws IOException {
        writer.append("<br/>");
    }

    @Override
    public void em() throws IOException {
        openTag("em");
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    private String loadResource(String resource) throws IOException {
        return IOUtils.toString(
                this.getClass().getResourceAsStream(
                        String.format("/com/machinelinking/render/%s", resource)
                )
        );
    }

    private String escapeStringMarkup(String in) {
        return in.replace("<", "&lt;").replace(">", "&gt").replace("&nbsp", " ");
    }

    private void merge(String key, String val, Map<String,String> inOut) {
        if(inOut.containsKey(key)) {
            if("style".equals(key)) {
                inOut.put(key, inOut.get(key) + "; " + val);
            } else {
                throw new IllegalArgumentException("Unsupported attribute merge for key " + key);
            }
        } else {
            inOut.put(key, val);
        }
    }

}
