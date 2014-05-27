package com.machinelinking.template.custom;

import com.machinelinking.render.HTMLWriter;
import com.machinelinking.template.EvaluationContext;
import com.machinelinking.template.TemplateCall;
import com.machinelinking.template.TemplateCallHandler;
import com.machinelinking.template.TemplateCallHandlerException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Main implements TemplateCallHandler {

    public static final String MAIN_TEMPLATE_NAME = "Main";

    private static final Map<String,String> MAIN_DIV_ATTR = new HashMap<String,String>(){{
        put("class", "main");
    }};

    @Override
    public boolean process(EvaluationContext context, TemplateCall call, HTMLWriter writer) throws TemplateCallHandlerException {
        if(! MAIN_TEMPLATE_NAME.equals(call.getName().asText())) return false;

        try {
            writer.openTag("span", MAIN_DIV_ATTR);
            writer.openTag("strong");
            writer.text("Main Article: ");
            writer.closeTag();
            for(String article : call.getParameterNames()) {
                final String domain = context.getJsonContext().getDocumentContext().getDocumentURL().getHost();
                writer.anchor(String.format("http://%s/wiki/%s", domain, article), article, true);
                writer.text(" ");
            }
            writer.closeTag();
            return true;
        } catch (IOException ioe) {
            throw new TemplateCallHandlerException("Error while invoking Main", ioe);
        }
    }
}