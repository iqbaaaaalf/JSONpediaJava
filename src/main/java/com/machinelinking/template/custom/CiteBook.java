package com.machinelinking.template.custom;

import com.machinelinking.render.HTMLWriter;
import com.machinelinking.template.EvaluationContext;
import com.machinelinking.template.TemplateCall;
import com.machinelinking.template.TemplateCallHandler;
import com.machinelinking.template.TemplateCallHandlerException;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <i>Cite Web</i> template render.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CiteBook implements TemplateCallHandler {

    public static final String CITE_BOOK_TEMPLATE_NAME = "[Cc]ite book";

    private static final Map<String,String> CITEBOOK_DIV_ATTR = new HashMap<String,String>(){{
        put("class", "cite-book");
    }};

    public static final String[] TEMPLATE_FIELDS = new String[] {
            "last", "first", "author", "date", "title", "url", "location", "publisher", "page", "isbn", "accessdate"
    };

    @Override
    public boolean process(EvaluationContext context, TemplateCall call, HTMLWriter writer)
    throws TemplateCallHandlerException {
        if(! call.getName().asText().matches(CITE_BOOK_TEMPLATE_NAME)) return false;
        try{
            writer.openTag("span", CITEBOOK_DIV_ATTR);
            writer.openTable("Cite Book", null);
            JsonNode value;
            for(String field: TEMPLATE_FIELDS) {
                value = call.getParameter(field);
                if(value == null) continue;
                writer.openTag("tr");
                writer.openTag("td");
                context.evaluate(field, value, writer);
                writer.closeTag();
                writer.closeTag();
            }
            writer.closeTable();
            writer.closeTag();
            return true;
        } catch (IOException ioe) {
            throw new TemplateCallHandlerException("Error while invoking Cite Book", ioe);
        }
    }
}