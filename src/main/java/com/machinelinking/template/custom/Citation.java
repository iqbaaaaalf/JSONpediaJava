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
 * <i>Citation</i> template render.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Citation implements TemplateCallHandler {

    public static final String CITATION_TEMPLATE_NAME = "Citation";

    private static final Map<String,String> CITATION_DIV_ATTR = new HashMap<String,String>(){{
        put("class", "citation");
    }};

    private static final String[] TEMPLATE_FIELDS = {
            "content",
            "author",
            "year",
            "publisher",
            "location",
            "pages",
            "name",
            "url",
            "accessdate",
            "isbn",
            "first",
            "last",
            "contribution",
            "format",
            "postscript",
    };

    @Override
    public boolean process(EvaluationContext context, TemplateCall call, HTMLWriter writer) throws TemplateCallHandlerException {
        try {
            if (!CITATION_TEMPLATE_NAME.equals(call.getName().asText())) return false;

            writer.openTag("span", CITATION_DIV_ATTR);
            writer.openTag("strong");
            writer.text("Citation: ");
            writer.closeTag();
            JsonNode value;
            for (String field : TEMPLATE_FIELDS) {
                value = call.getParameter(field);
                if (value == null) continue;
                context.evaluate(field, value, writer);
            }
            writer.closeTag();
            return true;
        } catch (IOException ioe) {
            throw new TemplateCallHandlerException("Error while invoking Citation.", ioe);
        }
    }
}