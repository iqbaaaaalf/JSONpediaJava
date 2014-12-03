/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

package com.machinelinking.extractor;

import com.machinelinking.pagestruct.WikiTextSerializerHandler;
import com.machinelinking.pagestruct.WikiTextSerializerHandlerFactory;
import com.machinelinking.parser.Attribute;
import com.machinelinking.parser.DefaultWikiTextParserHandler;
import com.machinelinking.render.DefaultDocumentContext;
import com.machinelinking.render.DefaultHTMLRender;
import com.machinelinking.render.DefaultHTMLRenderFactory;
import com.machinelinking.render.DocumentContext;
import com.machinelinking.render.NodeRenderException;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.template.RenderScope;
import com.machinelinking.util.JSONUtils;
import com.machinelinking.util.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

/**
 * Extracts the text content from the page, replaces any structured content with a special syntax containing JSON.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TextHandler extends DefaultWikiTextParserHandler {

    private static final Logger logger = Logger.getLogger(TextHandler.class);

    private final StringWriter writer;
    private final WikiTextSerializerHandler decoratedHandler;

    private final StringBuilder outputBuffer = new StringBuilder();

    private final DefaultHTMLRender render = DefaultHTMLRenderFactory.getInstance().createRender(false);

    private DocumentContext context;
    private int nestedStructures = 0;

    protected TextHandler() {
        writer = new StringWriter();
        final Serializer serializer;
        try {
            serializer = new JSONSerializer(writer);
        } catch (IOException ioe) {
            throw new IllegalStateException();
        }
        decoratedHandler = WikiTextSerializerHandlerFactory.getInstance().createSerializerHandler(serializer);
        decoratedHandler.reset();
    }

    public void reset() {
        nestedStructures = 0;
        decoratedHandler.reset();
        outputBuffer.delete(0, outputBuffer.length());
    }

    public String flushContent() {
        final String out = outputBuffer.toString();
        reset();
        return out.trim();
    }

    @Override
    public void beginDocument(URL documentURL) {
        this.context = new DefaultDocumentContext(RenderScope.TEXT_RENDERING, documentURL);
        reset();
    }

    @Override
    public void section(String title, int level) {
        //decoratedHandler.section(title, level);
    }

    @Override
    public void beginReference(String label) {
        nestedStructures++;
        decoratedHandler.beginReference(label);
    }

    @Override
    public void endReference(String label) {
        decoratedHandler.endReference(label);
        nestedStructures--;
    }

    @Override
    public void beginLink(URL url) {
        nestedStructures++;
        decoratedHandler.beginLink(url);
    }

    @Override
    public void endLink(URL url) {
        decoratedHandler.endLink(url);
        nestedStructures--;
    }

    @Override
    public void beginList() {
        nestedStructures++;
        decoratedHandler.beginList();
    }

    @Override
    public void listItem(ListType t, int level) {
        decoratedHandler.listItem(t, level);
    }

    @Override
    public void endList() {
        decoratedHandler.endList();
        nestedStructures--;
    }

    @Override
    public void beginTemplate(TemplateName name) {
        nestedStructures++;
        decoratedHandler.beginTemplate(name);
    }

    @Override
    public void endTemplate(TemplateName name) {
        decoratedHandler.endTemplate(name);
        nestedStructures--;
    }

    @Override
    public void beginTable() {
        // disabled // nestedStructures++;
        // disabled // decoratedHandler.beginTable();
    }

    @Override
    public void headCell(int row, int col) {
        // disabled // decoratedHandler.headCell(row, col);
    }

    @Override
    public void bodyCell(int row, int col) {
        // disabled // decoratedHandler.bodyCell(row, col);
    }

    @Override
    public void endTable() {
        // disabled // decoratedHandler.endTable();
        // disabled // nestedStructures--;
    }

    @Override
    public void parameter(String param) {
        decoratedHandler.parameter(param);
    }

    @Override
    public void text(String content) {
        if(nestedStructures < 0) throw new AssertionError("Invalid value, must be >= 0");
        if(nestedStructures > 0) {
            decoratedHandler.text(content);
        } else {
            decoratedHandler.flush();
            decoratedHandler.reset();
            final StringBuffer buffer = writer.getBuffer();
            if(buffer.length() > 0) {
                expandBuffer(buffer.toString(), outputBuffer);
                buffer.delete(0, buffer.length());
            }
            outputBuffer.append(content);
        }
    }

    @Override
    public void paragraph() {
        decoratedHandler.paragraph();
    }

    @Override
    public void beginTag(String name, Attribute[] attributes) {
        // disabled // decoratedHandler.beginTag(name, attributes);
    }

    @Override
    public void endTag(String name) {
        // disabled // decoratedHandler.endTag(name);
    }

    @Override
    public void inlineTag(String name, Attribute[] attributes) {
        // disabled // decoratedHandler.inlineTag(name, attributes);
    }

    @Override
    public void commentTag(String comment) {
        // disabled // decoratedHandler.commentTag(comment);
    }

    @Override
    public void entity(String form, char value) {
        // disabled // decoratedHandler.entity(form, value);
    }

    @Override
    public void var(Var v) {
       // disabled // decoratedHandler.var(v);
    }

    @Override
    public void italicBold(int level) {
        //disabled// decoratedHandler.italicBold(level);
    }

    @Override
    public void endDocument() {
        decoratedHandler.endDocument();
    }

    private void expandBuffer(String data, StringBuilder sb) {
        try {
            sb.append(expandStructure(data));
        } catch (Exception e) {
            logger.error(String.format("An error occurred while expanding data:\n%s\n", data), e);
            sb.append("<%");
            sb.append(data);
            sb.append("%>");
        }
    }

    private String expandStructure(String data) throws IOException, NodeRenderException {
        final JsonNode[] nodes = JSONUtils.parseJSONMulti(data);
        final StringBuilder sb = new StringBuilder();
        for(JsonNode node : nodes) {
            sb.append( render.renderFragment(context, node) );
        }
        return StringUtils.stripTags(sb.toString());
    }

}
